package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  public void AddTransactions (Transaction transaction) {
    if(firstTransaction == null && lastTransaction == null) {
      firstTransaction = lastTransaction = transaction;
    }
    lastTransaction.next = transaction;
    lastTransaction = transaction;
    numTransactions += 1;     
    return;                                                   
  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
    if(firstTransaction == null && lastTransaction == null)
      throw new EmptyQueueException();
    Transaction temp = firstTransaction;
    firstTransaction = firstTransaction.next;
    if(firstTransaction == null) lastTransaction = null;
    numTransactions -= 1;
    return temp;
  }

  public int size() {
    return numTransactions;
  }
}
