import java.security.*;

public class Wallet extends KeyGenerator {
    private static int newIdentifier = 1;
    private int identifier;
    public Wallet() {
        super();
        identifier = newIdentifier;
        newIdentifier++;
    }
    final class Transaction implements Cloneable, Comparable {
        private final PublicKey sender;
        private final PublicKey reciever;
        private final String signedMessage; 
        private final double amt;
        public Transaction(PublicKey sender, PublicKey reciever, String signedMessage, double amount) {
            this.sender = sender;
            this.signedMessage = signedMessage;
            this.amt = amount;
            this.reciever = reciever;
            
        }
        public Transaction(Wallet wallet, double amount) {
            this.reciever = wallet.getPublicKey();
            this.sender = null;
            this.signedMessage = null;
            this.amt = amount;
        }
        public Transaction(Wallet sender, PublicKey reciever, double amount) {
            this(sender.getPublicKey(), reciever, sender.encryptMessageUsingPrivateKey(String.valueOf(amount)), amount);
            BlockChain.addTransactions(this);
        }
        @Override
        public int compareTo(Object obj) {
            if (!(obj instanceof Transaction)) {
                Transaction transact = (Transaction) obj;
                int senderCompareTo = Math.abs(this.sender.hashCode()-transact.sender.hashCode())/(this.sender.hashCode()-transact.sender.hashCode());
                int recieverCompareTo = Math.abs(this.reciever.hashCode()-transact.reciever.hashCode())/(this.reciever.hashCode()-transact.reciever.hashCode());
                double amtCompareTo = Math.abs(this.amt-transact.amt)/(this.amt-transact.amt);
                if (senderCompareTo != 0) {
                    return senderCompareTo;
                }
                if (recieverCompareTo != 0) {
                    return recieverCompareTo;
                }
                if (amtCompareTo != 0) {
                    return (int) amtCompareTo;
                }
                return 0;
            }
            return -1;
        }        
        @Override 
        public Transaction clone() {
            Transaction copy = new Transaction(this.sender, this.reciever, this.signedMessage, this.amt);
            return copy;
        }
        @Override
        public int hashCode() {
            int hash = 0;
            hash = 31 * hash + (int) amt;
            hash = 31 * hash + (sender == null ? 0 : sender.hashCode());
            hash = 31 * hash + (reciever == null ? 0 : reciever.hashCode());
            return hash;
        }
        public String getSignedMessage() {
            return this.signedMessage;
        }
        public PublicKey getSender() {
            return this.sender;
        }
        public PublicKey getReciever() {
            return this.reciever;
        }
        public double getAmountSent() {
            return this.amt;
        }
    }
    public int getIdentifier() {
        return identifier;
    }
    @Override
    public String toString() {
        return String.format("Wallet %d", identifier);
    }
    public void transfer(double amt, PublicKey receiver) {
        new Transaction(this, receiver, amt);
    }
    public Transaction rewardAmount(double amt) {
        return new Transaction(this, amt);
    }
}