package notebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by cheviuk on 04.09.2015.
 */
public class NotebookView {

    private NotebookController nc = null;

    public NotebookView(NotebookController notebookController){
        this.nc = notebookController;
    }

    public void mainMenu() {
        Scanner scanner = new Scanner(System.in);
        int command = 0;

        while (true) {

            System.out.println();
            System.out.println("Please choose command:");
            System.out.println("1 - print all notes");
            System.out.println("2 - create new note");
            System.out.println("3 - delete note");
            System.out.println("4 - print info about note");
            System.out.println("5 - save to file");
            System.out.println("6 - load from file");
            System.out.println("7 - Exit");

            try {
                command = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException ex) {
                System.out.println("Wrong input!");
            }

            switch (command) {
                case 1:
                    this.printAll();
                    break;
                case 2:
                    System.out.println("Create new note:");
                    this.createNewNote();
                    break;
                case 3:
                    System.out.println("Delete note:");
                    this.deleteNote();
                    break;
                case 4:
                    System.out.println("Print note info:");
                    this.printNoteInfo();
                    break;
                case 5:
                    System.out.println("Save to file:");
                    this.saveToFile();
                    break;
                case 6:
                    System.out.println("Load from file:");
                    this.loadFromFile();
                    break;
                case 7:
                    System.out.println("Exit");
                    scanner.close();
                    return;
                default:
                    System.out.println("Incorrect command!");
                    break;
            }
        }
    }

    private void printAll() {
        if(nc.getNotes().size() <= 0) {
            System.out.println("There is no any notes to print.");
            return;
        }
        for(int i = 0; i<nc.getNotes().size(); i++) {
            System.out.println(Integer.toString(i + 1) + ": " + nc.getNotes().get(i).getSummary());
        }
    }

    private void createNewNote(){
        Scanner scanner = new Scanner(System.in);
        int command;

            try {
                System.out.println("Please select type of Note:");
                System.out.println("1: Note(simple note)");
                System.out.println("2: Task");
                System.out.println("3: Meeting");

                command = Integer.parseInt(scanner.nextLine());

                switch (command) {
                    case 1:
                        this.createNote();
                        break;
                    case 2:
                        this.createTask();
                        break;
                    case 3:
                        this.createMeeting();
                        break;
                    default:
                        System.out.println("Incorrect command!");
                        this.createNewNote();
                        break;
                }
            } catch (InputMismatchException | NumberFormatException ex) {
                System.out.println("Wrong input!");
            }

    }

    private void createNote(){
        String summary;
        String description;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Specify note summary: ");
        summary = scanner.nextLine();
        System.out.println("Specify note description: ");
        description = scanner.nextLine();

        nc.add(summary, description);
    }

    private void createTask(){
        String summary;
        String description;
        Date dueDate;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Specify note summary: ");
        summary = scanner.nextLine();
        System.out.println("Specify note description: ");
        description = scanner.nextLine();

        while (true) {
            System.out.println("Specify due date(yyyy-MM-dd): ");
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                dueDate = sdf.parse(scanner.nextLine());

                if(dueDate.before(new Date())){
                    System.out.println("Date cannot be before current date.");
                } else {
                    nc.add(summary, description, dueDate);
                    return;
                }
            }
            catch (ParseException pe) { System.out.println("Wring format!"); }
            catch (Exception ex) { System.out.println("Wrong input!"); }

        }

    }

    private void createMeeting(){
        String summary;
        String description;
        Date startTime;
        Date endTime;
        String place;
        List<Contact> contacts;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Specify note summary: ");
        summary = scanner.nextLine();
        System.out.println("Specify note description: ");
        description = scanner.nextLine();
        System.out.println("Specify place: ");
        place = scanner.nextLine();
        String startTimeString;
        String endTimeString;

        while (true) {
            try {
                System.out.println("Specify start time(dd-MM-yyyy hh:mm)");
                startTimeString = scanner.nextLine();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                startTime = sdf.parse(startTimeString);

                if (startTime.before(new Date())) {
                    System.out.println("Start Time should not be in past.");
                    throw new Exception("Start Time should not be in past.");
                } else {
                    break;
                }
            }
            catch (ParseException pe) {
                System.out.println("Wrong format!");
            }
            catch (NumberFormatException ex) {
                System.out.println("Wrong input!");
            }
            catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        while (true) {

            try {

                System.out.println("Specify end time(dd-MM-yyyy hh:mm)");
                endTimeString = scanner.nextLine();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                endTime = sdf.parse(endTimeString);
                if (endTime.before(startTime)) {
                    System.out.println("End Time should not be before start time.");
                    throw new Exception("End Time should not be before start time.");
                } else {
                    break;
                }
            }
            catch (ParseException pe) {
                System.out.println("Wrong format!");
            }
            catch (NumberFormatException ex) {
                System.out.println("Wrong input!");
            }
            catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        contacts = new ArrayList<>();

        System.out.println("Specify contact/s:");
        createContact(contacts);

        nc.add(summary, description, place, startTime, endTime, contacts);
    }

    private void createContact(List<Contact> contacts) {

        String name;
        String email;
        String phone;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Specify name: ");
        name = scanner.nextLine();

        while (true) {
            try {
                System.out.println("Specify email: ");
                email = scanner.nextLine();
                if(!validateEmail(email)){
                    throw new Exception("Not valid email!");
                }
                else {
                    break;
                }
            }
            catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

        while (true) {
            try {
                System.out.println("Specify phone: ");
                phone = scanner.nextLine();
                if(!validatePhone(phone)) {
                    throw new Exception("Not valid phone!");
                }
                else {
                    break;
                }
            }
            catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        contacts.add(nc.createContact(name, email, phone));

        while (true) {
            System.out.println("Do you want to add another contact? y/n");
            String answer = scanner.nextLine();
            if (answer.toLowerCase().equals("y")) {
                createContact(contacts);
                break;
            }
            else if(answer.toLowerCase().equals("n")){
                return;
            }
            else {
                System.out.println("Wrong input, try again.");
            }
        }
    }

    private void printNoteInfo(){
        if(nc.getNotes().size() <= 0){
            System.out.println("There is no any notes to print.");
            return;
        }

        while(true){

            try {
                Scanner scanner = new Scanner(System.in);
                System.out.println("Type id of note: ");
                int id = Integer.parseInt(scanner.nextLine()) - 1;
                nc.getNotes().get(id).print();
                return;
            }
            catch (IndexOutOfBoundsException ex) {
                System.out.println("Incorrect id!");
            }
            catch (InputMismatchException | NumberFormatException ex) {
                System.out.println("Wrong input!");
            }
        }
    }

    private void deleteNote(){
        if(nc.getNotes().size() <= 0) {
            System.out.println("There is no any notes to delete.");
            return;
        }
        while (true) {
            try{
                Scanner scanner = new Scanner(System.in);
                System.out.println("Type id of note: ");
                int id = Integer.parseInt(scanner.nextLine()) - 1;
                nc.delete(id);
                return;
            }
            catch (IndexOutOfBoundsException ex){
                System.out.println("Incorrect id!");
            }
            catch (InputMismatchException | NumberFormatException ex){
                System.out.println("Wrong input!");
            }
        }
    }

    private boolean validateEmail(String email){
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                                            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    private boolean validatePhone(String phone){
        Pattern pattern = Pattern.compile("^\\+([0-9\\-]?){9,11}[0-9]$");
        Matcher matcher = pattern.matcher(phone);

        return matcher.matches();
    }

    private void saveToFile(){
        try {
            nc.saveToFile();
        }
        catch (Exception ex){
            System.out.println("File is not saved.");
            ex.printStackTrace();
        }
    }

    private void loadFromFile(){
        try {
            nc.loadFromFile();
            System.out.println("Loading from file has been successfully completed.");
        }
        catch (FileNotFoundException ex) {
            System.out.println("File is absent.");
        }
        catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
