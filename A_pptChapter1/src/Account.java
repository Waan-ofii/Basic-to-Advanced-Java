import java.util.Scanner;

public class Account {
    double balance;
    double deposit;
    double withdraw;
    double Transaction;
    int Airtime;


    void setbalance(double baba) {
        balance = baba;
    }


    void setdeposit(double dip) {
        deposit = dip;
        balance=balance+deposit;

        System.out.println("your moneyis diposited successfully");
        System.out.println(" Tank you for using OUR BANK");
    }
    void setWithdraw(double with) {
        withdraw = with;
        balance=balance-withdraw;
        System.out.println("your money is withdrawed successfully");
        System.out.println(" Tank you for using OUR BANK");
    }



    void setTransaction(double trans) {
        withdraw = trans;
        balance=balance-Transaction;
        System.out.println("Transaction completed successfully");
        System.out.println(" Tank you for using OUR BANK");
    }
    void setAirtime(int card) {
        Airtime = card;
        balance=balance-Airtime;
        System.out.println("you full Airtime successfully");
        System.out.println(" Tank you for using OUR BANK");
    }



    void showbalance(){
        System.out.println("currently your balance is : "+(balance));
        System.out.println(" Tank you for using OUR BANK");

    }
}
class runner{
    public static void main(String[] args) {
        Account n=new Account();
        Scanner g=new Scanner(System.in);
        int choice;
        boolean maal=true;
        while(maal){
            System.out.println("  ACCOUNT MANAGEMEMNT  ");
            System.out.println("press 1:to see your balance ");
            System.out.println("press 2:to deposit money");
            System.out.println("press 3:to withdraw money");
            System.out.println("press 4:for Transaction");
            System.out.println("press 5:for Airtime");
            System.out.println("press 6:to Exit");
            System.out.print("\nplease enter your choice :");
            choice=g.nextInt();
            switch(choice){
                case 1:
                    n.showbalance();
                    break;
                case 2:
                    double dipo;
                    System.out.print("please eneter amount of money you wanna diposit : ");
                    dipo=g.nextDouble();
                    n.setdeposit(dipo);
                    break;
                case 3:
                    double withd;
                    System.out.print("please eneter amount of money you wanna withdraw : ");
                    withd=g.nextDouble();
                    n.setdeposit(withd);

                    break;
                case 4:
                    double tran;
                    System.out.print("please eneter amount of money you wanna Transfer : ");
                    tran=g.nextDouble();
                    n.setdeposit(tran);
                    break;
                case 5:
                    double air;
                    System.out.print(" enter amount of air time you want: ");
                    air=g.nextDouble();
                    n.setdeposit(air);
                    break;
                case 6:
                    maal=false;
                    break;
                default:
                    System.out.println("invalid option try again!!");

            }

        }

    }
}