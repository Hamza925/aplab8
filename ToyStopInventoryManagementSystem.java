package ApLab;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JOptionPane;
import java.sql.*;

/**
 *
 * @author Fahad Satti
 */
public class ToyStopInventoryManagementSystem implements java.io.Serializable {
    ToyStopService tsService = new ToyStopService();
    public void init(){
        final String JDBC_DRIVER = "com.mysql.jdbc.driver";
        final String user="root";
        final String pass="root";
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:1527/sample", "user","pass");
        
        tsService.initEmployees();
        tsService.initStores();
        tsService.initToys();
        System.out.println("Init complete");
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
       
        ToyStopInventoryManagementSystem tsims = new ToyStopInventoryManagementSystem();
        
        //load previous data
        try
        {
            File file_st = new File("stores.ser");
            File file_emp = new File("emp.ser");
            if(file_st.exists() && file_emp.exists())
            { 
                FileInputStream input = new FileInputStream("stores.ser");
                ObjectInputStream outStream = new ObjectInputStream(input);
                tsims.tsService.stores = (ArrayList) outStream.readObject();
                outStream.close();
                input.close();

                input = new FileInputStream("employee.ser");
                outStream = new ObjectInputStream(input);
                tsims.tsService.employees = (ArrayList) outStream.readObject();
                outStream.close();
                input.close();
            }
         }
        catch(IOException ioe)
        {
             ioe.printStackTrace();
             return;
        }
        catch(ClassNotFoundException c)
        {
             System.out.println("The data not found");
             c.printStackTrace();
             return;
        }
        
        //checking if no data imported from file then initialize
        if(tsims.tsService.stores.isEmpty())
        {
            System.out.println("No data found in the file so initializing the system!");
            tsims.init();
        }
        
        
        //checking if all stores have at least one employee
        for (int i = 0; i < 100; i++)
        {
            Store tempStore = new Store();
            tempStore = tsims.tsService.stores.get(i);
            if(tempStore.employees.isEmpty())
            {
                int storeID = tempStore.getUID();
                System.out.println(storeID + " has no employee assigned!");
            }
        }
        int option=1;
        while (option==1){
            
        tsims.showMenu();
        Scanner reader = new Scanner(System.in);
        int opt = reader.nextInt();
        switch(opt)
        {
            //save current state of the system to the files
            case 0:
                tsims.saveData(tsims.tsService.stores, tsims.tsService.employees);
                break;
                
            //show all data
            case 1:
                tsims.printAll();
                break;
                
            //add a new store
            case 2:
                int newStoreID = tsims.tsService.addStore();
                System.out.println("The Store is Added.");
                System.out.println();
                System.out.println("Store ID: " + newStoreID);
                break;
                
            //add a new employee
            case 3:
                int newEmpID = tsims.tsService.addEmployee();
                System.out.println("The Employee is  Added.");
                System.out.println();
                System.out.println("Store ID: " + newEmpID);
                break;
                
            //add a new toy
            case 4:
                Toy newToy = new Toy();
                newToy.setUID(Util.getSaltNum(-1));
                newToy.setMinAge(Util.getSaltNum(1));
                newToy.setMaxAge(Util.getSaltNum(18));
                newToy.setPrice(Util.getSaltNum(1000));
                newToy.setName(Util.getSaltAlphaString());
                newToy.setAddedOn(LocalDateTime.now());

                Random randStore = new Random();
                int index = randStore.nextInt(tsims.tsService.stores.size());
                Store selectedStore = (Store)tsims.tsService.stores.get(index);
                selectedStore.addToy(newToy);
                break;
                
            default:
                System.out.println("you choose the Wrong Option!");
        }
        }
        
    }
    
    private void saveData(ArrayList<Store> stores, ArrayList<Employee> employees)
    {
        try 
        {
            //serializing stores arraylist
            FileOutputStream storeOut = new FileOutputStream("stores.ser");
            ObjectOutputStream outputStream = new ObjectOutputStream(storeOut);
            outputStream.writeObject(stores);
            outputStream.close();
            storeOut.close();

            //serializing employees arraylist
            FileOutputStream empOut = new FileOutputStream("employee.ser");
            outputStream = new ObjectOutputStream(empOut);
            outputStream.writeObject(employees);
            outputStream.close();
            empOut.close();
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    private void showMenu() {
        System.out.println("Welcome to Toy Stop Inventory Management System");
        System.out.println("Enter 1 to show all data");
        System.out.println("Enter 2 to add a new Store");
        System.out.println("Enter 3 to add a new Employee");
        System.out.println("Enter 4 to add a new Toy");
        System.out.println("Enter 0 to save state");
    }

    private void printAll() {
        System.out.println(this.tsService.stores);
    }
    
}
