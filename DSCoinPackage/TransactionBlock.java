package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  TransactionBlock() {}
  
  TransactionBlock(Transaction[] t) {
    this.trarray = new Transaction[t.length];
    for(int i=0; i<t.length; i++){
      this.trarray[i] = new Transaction();
      this.trarray[i].coinID = t[i].coinID;
      this.trarray[i].Source = t[i].Source;
      this.trarray[i].Destination = t[i].Destination;
      this.trarray[i].coinsrc_block = t[i].coinsrc_block;
      this.trarray[i].next = t[i].next;
    }
    this.previous = null;
    MerkleTree tree = new MerkleTree();
    tree.Build(trarray);
    this.Tree = tree;
    this.trsummary = this.Tree.rootnode.val;
    this.dgst = null;
  }

  public boolean checkTransaction (Transaction t) {
    if(t.coinsrc_block == null)
      return true;
    TransactionBlock curr = this.previous;
    while(curr != null) {
      if(curr == t.coinsrc_block) {
        for(Transaction tr : curr.trarray) {
          if(tr.coinID.equals(t.coinID) && tr.Destination.UID.equals(t.Source.UID)) 
            return true;
        }
        return false;
      }
      for(Transaction tr : curr.trarray) {
        if(tr.coinID.equals(t.coinID)) {
          return false;
        }
      }
      curr = curr.previous;
    }
    return true;
  }



  public boolean checkTransactionMINE (Transaction t) {
    if(t.coinsrc_block == null)
      return true;
    TransactionBlock curr = this; //pseudo curr.previous, beacuse curr is YET to be made
    while(curr != null) {
      if(curr == t.coinsrc_block) {
        for(Transaction tr : curr.trarray) {
          if(tr.coinID.equals(t.coinID) && tr.Destination.UID.equals(t.Source.UID))
            return true;
        }
        return false;
      }
      for(Transaction tr : curr.trarray) {
        if(tr.coinID.equals(t.coinID)) {
          return false;
        }
      }
      curr = curr.previous;
    }
    return true;
  }
}
