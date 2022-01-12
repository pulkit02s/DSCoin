package DSCoinPackage;
import HelperClasses.*;

public class Moderator {


  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {
    Members mod = new Members("Moderator");
    int start = 100000;
    int member_cnt = 0;
    for(int i=0; i<coinCount; i++) {
      Transaction tr = new Transaction();
      tr.coinID = String.valueOf(start);
      tr.Source = mod;
      tr.coinsrc_block = null;
      tr.Destination = DSObj.memberlist[member_cnt];          ///// okay?
      DSObj.pendingTransactions.AddTransactions(tr);
      member_cnt = (member_cnt + 1)%DSObj.memberlist.length;   /// ??
      start++;
    }
    member_cnt = 0;
    for(int j=0; j<coinCount/DSObj.bChain.tr_count; j++) { 
      // System.out.println(DSObj.pendingTransactions.size());
      TransactionBlock tB = new TransactionBlock();
      Transaction[] arr = new Transaction[DSObj.bChain.tr_count];
      for(int i=0; i<DSObj.bChain.tr_count; i++) { //creating a transaction block
        try{
          Transaction tr = DSObj.pendingTransactions.RemoveTransaction();
          DSObj.memberlist[member_cnt].mycoins.add(new Pair<String, TransactionBlock>(tr.coinID, tB));
          arr[i] = tr;
          member_cnt = (member_cnt + 1)%DSObj.memberlist.length;   /// ??
        } catch(EmptyQueueException e){}
      } 
      tB.trarray = arr;
      MerkleTree tree = new MerkleTree();
      tree.Build(tB.trarray);
      tB.Tree = tree;
      tB.trsummary = tB.Tree.rootnode.val;
      DSObj.bChain.InsertBlock_Honest(tB);
    }
    start--;
    DSObj.latestCoinID = String.valueOf(start);
  }
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {
    Members mod = new Members("Moderator");
    int start = 100000;
    int member_cnt = 0;
    for(int i=0; i<coinCount; i++) {
      Transaction tr = new Transaction();
      tr.coinID = String.valueOf(start);
      tr.Source = mod;
      tr.coinsrc_block = null;
      tr.Destination = DSObj.memberlist[member_cnt];          ///// okay?
      DSObj.pendingTransactions.AddTransactions(tr);
      member_cnt = (member_cnt + 1)%DSObj.memberlist.length;   /// ??
      start++;
    }
    member_cnt = 0;
    for(int j=0; j<coinCount/DSObj.bChain.tr_count; j++) { 
      // System.out.println(DSObj.pendingTransactions.size());
      TransactionBlock tB = new TransactionBlock();
      Transaction[] arr = new Transaction[DSObj.bChain.tr_count];
      for(int i=0; i<DSObj.bChain.tr_count; i++) { //creating a transaction block
        try{
          Transaction tr = DSObj.pendingTransactions.RemoveTransaction();
          DSObj.memberlist[member_cnt].mycoins.add(new Pair<String, TransactionBlock>(tr.coinID, tB));
          // System.out.println("Destination " + DSObj.memberlist[member_cnt].UID + " coinID " + tr.coinID);
          // System.out.println("Destination " + DSObj.memberlist[member_cnt].UID + " coinID " + DSObj.memberlist[member_cnt].mycoins.get(j).first);
          arr[i] = tr;
          member_cnt = (member_cnt + 1)%DSObj.memberlist.length;   /// ??
        } catch(EmptyQueueException e){}
      } 
      tB.trarray = arr;
      MerkleTree tree = new MerkleTree();
      tree.Build(tB.trarray);
      tB.Tree = tree;
      tB.trsummary = tB.Tree.rootnode.val;
      // System.out.println(tB.trsummary);
      // System.out.println(tB.Tree.rootnode.val);
      DSObj.bChain.InsertBlock_Malicious(tB);
    }
    start--;
    DSObj.latestCoinID = String.valueOf(start);
  }

  // public void initializeDSCoin1(DSCoin_Honest DSObj, int coinCount) {
  //   Members mod = new Members("Moderator");
  //   int start = 100000;
  //   int member_cnt = 0;
  //   for(int j=0; j<coinCount/DSObj.bChain.tr_count; j++) { //this assumes every block must have tr_count coins
  //                                                           //i.e. coinCount is divisible bt tr_count
  //     TransactionBlock tB = new TransactionBlock();
  //     Transaction[] arr = new Transaction[DSObj.bChain.tr_count];
  //     for(int i=0; i<DSObj.bChain.tr_count; i++) { //creating a transaction block
  //       Transaction tr = new Transaction();
  //       tr.coinID = String.valueOf(start);
  //       tr.Source = mod;
  //       tr.coinsrc_block = null;
  //       tr.Destination = DSObj.memberlist[member_cnt];          ///// okay?
  //       System.out.println("Destination " + DSObj.memberlist[member_cnt].UID + " coinID " + tr.coinID);
  //       DSObj.memberlist[member_cnt].mycoins.add(new Pair<String, TransactionBlock>(tr.coinID, tB));
  //       arr[i] = tr;
  //       member_cnt = (member_cnt + 1)%DSObj.memberlist.length;   /// ??
  //       start++;
  //     } 
  //     //NO NOT LIKE THIS!!!!!!!!!!!!!!!!!!!!!!!!!!!, FIRST IN PENDING TRANSACTIONS
  //     tB.trarray = arr;
  //     MerkleTree tree = new MerkleTree();
  //     tree.Build(tB.trarray);
  //     tB.Tree = tree;
  //     tB.trsummary = tB.Tree.rootnode.val;
  //     DSObj.bChain.InsertBlock_Honest(tB);
  //     System.out.println(DSObj.pendingTransactions);
  //   }

  //   start--;
  //   DSObj.latestCoinID = String.valueOf(start);
  // }
}
