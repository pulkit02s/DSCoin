package DSCoinPackage;

import java.util.*;
import HelperClasses.*;

public class Members
 {

  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins = new ArrayList<Pair<String, TransactionBlock>>();
  public Transaction[] in_process_trans = new Transaction[100];
  public int len_ipt; 

  Members() {}

  Members(String s) {
    this.UID = s;
  }
  public void coins() {
    for(Pair p : this.mycoins) {
      System.out.print(p.first + "  ");
    }
  }
  public void insort() {
    List<Pair<String, TransactionBlock>> lst = this.mycoins;
    for(int i=0; i<lst.size(); i++) {
      int j = i;
      while(j >= 1 && lst.get(j).first.compareTo(lst.get(j-1).first) < 0){
        Collections.swap(lst, j, j-1);
        j--;
      }
    }
  }

  public void initiateCoinsend(String destUID, DSCoin_Honest DSObj) {
    Members dest = new Members();
    for(int i=0; i<DSObj.memberlist.length; i++) {
      if(DSObj.memberlist[i].UID.equals(destUID))
        dest = DSObj.memberlist[i];
    }
    Pair<String, TransactionBlock> coin = this.mycoins.get(0);
    this.mycoins.remove(0);
    Transaction tobj = new Transaction();
    tobj.coinID = coin.first; ////
    tobj.Source = this;
    tobj.Destination = new Members();
    tobj.Destination = dest;
    tobj.coinsrc_block = coin.second; 
    this.in_process_trans[len_ipt] = tobj;
    this.len_ipt += 1;
    DSObj.pendingTransactions.AddTransactions(tobj);
  }


  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
    
    //for updating mycoins list, 
      //first find tB of tobj, then 
      // tobj.Destination.mycoins.add(new Pair<String, TransactionBlock>(tobj.coinID, tB));
    TransactionBlock curr = DSObj.bChain.lastBlock;
    TransactionBlock tB = null; int i = 0;
    while(tB == null && curr != null) {
      for(i=0; i<curr.trarray.length; i++) {
        if(tobj == curr.trarray[i]) {
          tB = curr;
          tobj.Destination.mycoins.add(new Pair<String, TransactionBlock>(tobj.coinID, tB));
          tobj.Destination.insort();
          break;
        }
      }
      curr = curr.previous;
    }
    if(tB == null) 
      throw new MissingTransactionException();
    List<Pair<String, String>> lst1 = tB.Tree.QueryDocument(i);
    List<Pair<String, String>> lst2 = new ArrayList<Pair<String,String>>();
    curr = DSObj.bChain.lastBlock;

    while(curr != tB) {
      lst2.add(0, new Pair<String, String>(curr.dgst, curr.previous.dgst+"#"+curr.trsummary+"#"+curr.nonce));
      curr = curr.previous;
    }
    //curr points to tB now
    if(curr.previous != null) {
      lst2.add(0, new Pair<String, String>(curr.dgst, curr.previous.dgst+"#"+curr.trsummary+"#"+curr.nonce));
      curr = curr.previous;
      lst2.add(0, new Pair<String, String>(curr.dgst, null));
    }
    else {
      lst2.add(0, new Pair<String, String>(curr.dgst, DSObj.bChain.start_string+"#"+curr.trsummary+"#"+curr.nonce));
      curr = curr.previous;
      lst2.add(0, new Pair<String, String>(DSObj.bChain.start_string, null));
    }
    
    for(int j=0; j<this.in_process_trans.length; j++) {
      if(this.in_process_trans[j] == tobj) {
        while(this.in_process_trans[j] != null) {
          this.in_process_trans[j] = this.in_process_trans[j+1];
          j++;
        }
        break;
      }
    }
    this.len_ipt -= 1;

    return new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(lst1, lst2);
  }

  public void MineCoin(DSCoin_Honest DSObj) {
    TransactionBlock tB = new TransactionBlock();
    List<Transaction> v = new ArrayList<Transaction>();
    while(v.size() != DSObj.bChain.tr_count-1) {
      try{
        Transaction tr = DSObj.pendingTransactions.RemoveTransaction();
        boolean valid = true;
        valid = DSObj.bChain.lastBlock.checkTransactionMINE(tr);
        for(Transaction trans : v){
          if(trans.coinID.equals(tr.coinID)){
            valid = false;
          }
        }
        if(!valid)
          continue;

        v.add(tr);
        
      } catch(EmptyQueueException e) {} 
    }
    Transaction minerRewardTransaction = new Transaction();
    DSObj.latestCoinID = String.valueOf(Integer.parseInt(DSObj.latestCoinID) + 1);
    minerRewardTransaction.coinID = DSObj.latestCoinID;
    minerRewardTransaction.Source = null;
    minerRewardTransaction.coinsrc_block = null;
    minerRewardTransaction.Destination = this; 
    v.add(minerRewardTransaction);
    Transaction[] arr = new Transaction[v.size()];
      //THIS ASSUMES THE SIZE OF LIST NOW IS tr_count;
    for(int i=0; i<v.size(); i++){
      arr[i] = v.get(i);
    }
    tB.trarray = arr;
    MerkleTree tree = new MerkleTree();
    tree.Build(tB.trarray);
    tB.Tree = tree;
    tB.trsummary = tB.Tree.rootnode.val;
    DSObj.bChain.InsertBlock_Honest(tB); //all this for updating my transcations here

    this.mycoins.add(new Pair<String, TransactionBlock>(DSObj.latestCoinID, tB));       
  }  

  public void MineCoin(DSCoin_Malicious DSObj) {
    TransactionBlock tB = new TransactionBlock();
    List<Transaction> v = new ArrayList<Transaction>();
    while(v.size() != DSObj.bChain.tr_count-1) {
      try{
        Transaction tr = DSObj.pendingTransactions.RemoveTransaction();
        boolean valid = true;
        valid = DSObj.bChain.FindLongestValidChain().checkTransactionMINE(tr);
        for(Transaction trans : v){
          if(trans.coinID.equals(tr.coinID)){
            valid = false;
          }
        }
        if(!valid)
          continue;

        v.add(tr);
      } catch(EmptyQueueException e) {} 
    }
    Transaction minerRewardTransaction = new Transaction();
    DSObj.latestCoinID = String.valueOf(Integer.parseInt(DSObj.latestCoinID) + 1);
    minerRewardTransaction.coinID = DSObj.latestCoinID;
    minerRewardTransaction.Source = null;
    minerRewardTransaction.coinsrc_block = null;
    minerRewardTransaction.Destination = this; 
    v.add(minerRewardTransaction);
    Transaction[] arr = new Transaction[v.size()];
      //THIS ASSUMES THE SIZE OF LIST NOW IS tr_count;
    for(int i=0; i<v.size(); i++){
      arr[i] = v.get(i);
    }
    tB.trarray = arr;
    MerkleTree tree = new MerkleTree();
    tree.Build(tB.trarray);
    tB.Tree = tree;
    tB.trsummary = tB.Tree.rootnode.val;
    DSObj.bChain.InsertBlock_Malicious(tB); //all this for updating my transcations here

    this.mycoins.add(new Pair<String, TransactionBlock>(DSObj.latestCoinID, tB));       
  }  
  // public void initiateCoinsend(String destUID, DSCoin_Malicious DSObj) {
  //   Members dest = new Members();
  //   for(int i=0; i<DSObj.memberlist.length; i++) {
  //     if(DSObj.memberlist[i].UID.equals(destUID))
  //       dest = DSObj.memberlist[i];
  //   }
  //   Pair<String, TransactionBlock> coin = this.mycoins.get(0);
  //   this.mycoins.remove(0);
  //   Transaction tobj = new Transaction();
  //   tobj.coinID = coin.first; ////
  //   tobj.Source = this;
  //   tobj.Destination = new Members();
  //   tobj.Destination = dest;
  //   // System.out.println(tobj.Destination);
  //   tobj.coinsrc_block = coin.second; ////
  //   this.in_process_trans[len_ipt] = tobj;
  //   this.len_ipt += 1;
  //   DSObj.pendingTransactions.AddTransactions(tobj);
  // }
}
