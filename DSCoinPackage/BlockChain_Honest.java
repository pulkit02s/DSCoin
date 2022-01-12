package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  public void InsertBlock_Honest (TransactionBlock newBlock) {
    CRF obj = new CRF(64);
    newBlock.previous = lastBlock;
    String str;
    if(lastBlock == null) str = start_string;
    else   str = lastBlock.dgst;

    long start = 1000000001;
    while(true) {
      newBlock.dgst = obj.Fn(str + "#" + newBlock.trsummary + "#" + String.valueOf(start));
      if(newBlock.dgst.substring(0, 4).equals("0000")) {
        newBlock.nonce = String.valueOf(start);
        break;
      }
      start++;
    }
    lastBlock = newBlock;
    return;
  }
}
