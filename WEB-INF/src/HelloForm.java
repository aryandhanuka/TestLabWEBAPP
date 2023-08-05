import java.io.*;
import java.text.SimpleDateFormat;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.Date;

public class HelloForm extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // set content type
        response.setContentType("text/html");
        // get details of the form and handle them accordingly
        int req_id = Integer.parseInt(request.getParameter("uid"));
        int pid = 0;
        java.sql.Date dateS = null;
        String date;
        try {
            switch (req_id) {
                case 2:
                    // registration
                    String name = request.getParameter("name");
                    String addr = request.getParameter("address");
                    date = request.getParameter("dob");
                    char gen;
                    if (request.getParameter("gender") == "male") {
                        gen = 'm';
                    } else {
                        gen = 'f';
                    }
                    dateS = convert(date);
                    // call the method with the parameter values
                    regPatient(name,addr,gen,dateS, response);
                    break;
                // print a link to the welcome page
                case 3:
                    // cancellation
                    int aid = Integer.parseInt(request.getParameter("Appointment_id"));
                    pid = Integer.parseInt(request.getParameter("patient_id"));
                   canAppointment(pid,aid,response);
                    break;
                case 4:
                    // invoice
                    pid = Integer.parseInt(request.getParameter("Patient_id"));
                    invoice(pid,response);
                    break;
                case 5:
                    // appointment
                    pid = Integer.parseInt(request.getParameter("patient_id"));
                    int sid = Integer.parseInt(request.getParameter("Service_id"));
                    date = request.getParameter("doa");
                    dateS = convert(date);
                    appointment(pid,sid,dateS,response);
                    break;
                    case 6:
                    //make payment
                     pid = Integer.parseInt(request.getParameter("patient_id"));
                    makePayment(pid,response);
                    break;
                default:
                    throw new Exception("Invalid form submitted");

            }
        } catch (Exception e) {
            // display the error page
            PrintWriter out=response.getWriter();
             String docType = "<!doctype html public \" -//w3c//dtd html4.0 " +
                "transitional//en\">n\n";
            out.println(docType + "<html>\n" +
                    "<head>" +
                    "<link rel=\"stylesheet\" href=\"styles.css\">" +
                    "</head>" +
                    "<body>" +
                    "<h1>" +
                    e+
                    "</h1>" +
                    "<a href=\"Welcome.html\"><button>Back</button></a>" +
                    "</body>" +
                    "</html>");
        }

    }

    private Date convert(String date) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date startDate = sdf.parse(date);
            java.sql.Date dateS = new java.sql.Date(startDate.getTime());
            return dateS;
        } catch (Exception e) {
            throw e;
        }
    }
    private void regPatient(String name,String addr, char gen, java.sql.Date s,HttpServletResponse response ) throws ServletException, IOException, Exception
    {
        Patient p=new Patient(name,addr,s,gen);
        PrintWriter out=response.getWriter();
        String docType = "<!doctype html public \" -//w3c//dtd html4.0 " +
                "transitional//en\">n\n";
                    //print a thank you for registering with us message
                    out.println(docType+"<html>\n" +
                    "<head>" +
                    "<link rel=\"stylesheet\" href=\"styles.css\">" +
                    "</head>" +
                    "<body>"+
                    "<p>"+
                    "Thank you for registering with us "+ name +
                    "</p>"+
                    "<p>"+
                    "Your details are : \n"+
                    "<br>"+
                    "Address: "+addr+"\n"+
                    "</br><br>"+
                     "gender: "+gen +"\n"+
                      "</br><br>"+
                     "Date of birth: "+ s + "\n"+
                      "</br><br>"+
                     "<b>Patient id:"+ p.patient_id +"</b></p>"+
                      "</br>"+
                     "<a href=\"Welcome.html\"><button>Back</button></a>"+
                     "</body></html>");
    }
    private void canAppointment(int pid, int aid,HttpServletResponse response) throws ServletException, IOException, Exception
    {
        PrintWriter out=response.getWriter();
        String appointmentDets=getAppointment(pid,aid);
        String docType = "<!doctype html public \" -//w3c//dtd html4.0 " +
                "transitional//en\">n\n";
         out.println(docType+"<html>\n" +
                    "<head>" +
                    "<link rel=\"stylesheet\" href=\"styles.css\">" +
                    "</head>" +
                    "<body>"+
                    "<p>"+
                    "Thank you for letting us know "+
                    "</p>"+
                    "<p>"+
                      "We cancelled the following appointment:<br> "+appointmentDets+
                      "</br></p>"+
                     "<a href=\"Welcome.html\"><button>Back</button></a>"+
                     "</body></html>");      

        //print a confirmation of the cancellation
        //first get the details of the appointment that has been cancelled 
        //create a link to the other pages 
    }
    private void appointment(int pid, int sid, java.sql.Date s,HttpServletResponse response) throws ServletException, IOException, Exception
    {
        PrintWriter out=response.getWriter();
        String docType = "<!doctype html public \" -//w3c//dtd html4.0 " +
                "transitional//en\">n\n";
        //print a confirmation of the appointment
        //get the service_id, name of the service etc 
        //create a link to go back 
        int appointment_id=Patient.make_appointment(pid,sid,s);
        String appointmentDets=getAppointment(pid,appointment_id);
         out.println(docType+"<html>\n" +
                    "<head>" +
                    "<link rel=\"stylesheet\" href=\"styles.css\">" +
                    "</head>" +
                    "<body>"+
                    "<p>"+
                    "Thank you for choosing us."+
                    "<b> Kindly note that your appointment id is "+appointment_id+"</b>"+
                    "</p>"+
                    "<p>"+
                    "Your appointment details are :<br>"+
                    appointmentDets+
                    "</br></p>"+
                     "<a href=\"Welcome.html\"><button>Back</button></a>"+
                     "</body></html>");
    }
    private void invoice(int pid,HttpServletResponse response) throws ServletException, IOException, Exception
    {
        PrintWriter out=response.getWriter();
        String docType = "<!doctype html public \" -//w3c//dtd html4.0 " +
                "transitional//en\">n\n";
        //create a button to make the payment in the output page 
        //this button should use the dbConnection class to settle all the dues
        //the patient information should be present as a hidden tag on the invoice page 
        //the make payment page should have its own output page 
         out.println(docType+"<html>\n" +
                    "<head>" +
                    "<link rel=\"stylesheet\" href=\"styles.css\">" +
                    "</head>" +
                    "<body>"+
                    "Your invoice is as follows"+
                    "<p><br>"+
                    Patient.get_invoice(pid)+
                    "</br></p>"+
                    "<form action=\"HelloForm\" method=\"get\">"+
                    "<input type=\"submit\" value=\"Make payment\">"+
                    "<input type=\"hidden\" id=\"unique_id\" name=\"uid\" value=\"6\">"+
                    "<input type=\"hidden\" id=\"integerInput\" name=\"patient_id\" value=\""+pid+"\">"+
                    "</form>"+
                     "<a href=\"Welcome.html\"><button>Back</button></a>"+
                     "</body></html>");
        
    }
    private void makePayment(int pid,HttpServletResponse response) throws ServletException, IOException, Exception
    {
          PrintWriter out=response.getWriter();
          Patient.db.clearDues(pid);
        String docType = "<!doctype html public \" -//w3c//dtd html4.0 " +
                "transitional//en\">n\n";
                 out.println(docType+"<html>\n" +
                    "<head>" +
                    "<link rel=\"stylesheet\" href=\"styles.css\">" +
                    "</head>" +
                    "<body>"+
                    "<p>"+
                    "Thank you for choosing us.\n"+
                    " We have recieved your payment."+
                    "</p>"+
                     "<a href=\"Welcome.html\"><button>Back</button></a>"+
                     "</body></html>");
            
    }
    private String getAppointment(int pid, int rid) throws Exception
    {
        String output= Patient.db.viewAppointments(pid);
        int indexOf=output.indexOf("Appointment_id: "+rid);
        if(indexOf==-1)
        {
            throw new Exception("could not find the appointment");
        }
        output=output.substring(indexOf);
        int endIndex=output.indexOf('\n');
        if(endIndex==-1)
        {
            throw new Exception("could not find the appointment");
        }
        output=output.substring(0,endIndex);
        return output;
    }
}
