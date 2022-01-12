package DSCoinPackage;

import HelperClasses.CRF;
import HelperClasses.MerkleTree;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList = new TransactionBlock[100];
  public int len_lastBlocksList = 0;

  public static boolean checkTransactionBlock (TransactionBlock tB) {
    CRF obj = new CRF(64);
    boolean ans = tB.dgst.substring(0,4).equals("0000");
    MerkleTree tree = new MerkleTree();
    tree.Build(tB.trarray);
    ans &= (tB.trsummary.equals(tree.rootnode.val));

    if(tB.previous == null)
      ans &= (tB.dgst.equals(obj.Fn(start_string + "#" + tB.trsummary + "#" + tB.nonce)));
    else
      ans &= (tB.dgst.equals(obj.Fn(tB.previous.dgst + "#" + tB.trsummary + "#" + tB.nonce)));
    
    for(Transaction tr : tB.trarray) {
      ans &= tB.checkTransaction(tr);
    }
    return ans;
  }
  public TransactionBlock FindLongestValidChain () { 
    int max = -1;
    TransactionBlock ans = this.lastBlocksList[0];
    for(int i=0; i<len_lastBlocksList; i++) {
      TransactionBlock curr = lastBlocksList[i];
      TransactionBlock temp = lastBlocksList[i];
      int len = 0;
      while(curr != null) {
        if(checkTransactionBlock(curr) == true)
          len++;
        else {
          temp = curr.previous;
          len = 0;
        }
        curr = curr.previous;
      }
      if(len > max)
        ans = temp;
    }
    return ans;
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock) {
    TransactionBlock lastBlock = this.FindLongestValidChain();
    CRF obj = new CRF(64);
    newBlock.previous = lastBlock;
    String str;
    if(lastBlock == null) str = start_string;
    else str = lastBlock.dgst;

    long start = 1000000001;
    while(true) {
      newBlock.dgst = obj.Fn(str + "#" + newBlock.trsummary + "#" + String.valueOf(start));
      if(newBlock.dgst.substring(0, 4).equals("0000")) {
        newBlock.nonce = String.valueOf(start);
        break;
      }
      start++;
    }
    for(int i=0; i<len_lastBlocksList; i++){
      if(lastBlocksList[i] == lastBlock){ //longestvaliidchain ka last is (the ending one) then no need to make new branch
        lastBlocksList[i] = newBlock;
        return;                                
      }
    }
    int i=0;
    while(lastBlocksList[i] != null)
      i++;
    lastBlocksList[i] = newBlock;
    len_lastBlocksList = i+1;
  }
}
