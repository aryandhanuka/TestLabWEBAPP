import java.sql.*;

public class DBconnection {
    Connection connection;
    Statement statement;
    /**
     * basic constructor
     */
    public DBconnection() {
        connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/Administrative_data",
                    "root",
                    "123456");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    /**
     * this method is only used for queries that yield some result
     * @param query the query string
     * @return the resultSet
     * @throws Exception
     */
    private ResultSet askQuery(String query) throws Exception {
        try {
            statement = connection.createStatement();
            ResultSet resultSet;
            resultSet = statement.executeQuery(query);
            return resultSet;
        } catch (Exception e) {
            throw e;
        }
    }
private void checkPatient(int pid) throws Exception{
	ResultSet resultSet= askQuery("SELECT * FROM ADMINISTRATIVE_DATA.Patients"+
	" WHERE patient_id = "+pid);
	int i=0;
	while(resultSet.next())
{
i++;
}
	resultSet.close();
	statement.close();
	if (i==0)
	{
		throw new Exception("No such patient found");
	}
	}
private void checkService(int sid) throws Exception{
        ResultSet resultSet= askQuery("SELECT * FROM ADMINISTRATIVE_DATA.Services"+
        " WHERE service_id = "+sid);
        int i=0;
        while(resultSet.next())
{
i++;
}
        resultSet.close();
        statement.close();
        if(i==0)
{
throw new Exception(" No such service found");
}
        }
private void checkAppointment(int rid) throws Exception{
        ResultSet resultSet= askQuery("SELECT * FROM ADMINISTRATIVE_DATA.Appointments"+
        " WHERE request_id = "+rid);
        int i=0;
        while(resultSet.next())
{
i++;
}
        resultSet.close();
        statement.close();
        if(i==0)
{
throw new Exception(" No such Appointment found");
}
        }
    /**
     *  used for updating the database queries
     * @param query the query string
     * @return an int representing the number of rows affected
     * @throws Exception
     */
    private int askUpdate(String query) throws Exception {
        try {
            statement = connection.createStatement();
            int returnVal=statement.executeUpdate(query);
            return returnVal;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 
     * @param name of patient
     * @param addr of patient
     * @param dob  of patient
     * @param gen  of patient
     * @return the assigned patient_id of the patient
     */
    public int registerPatient(String name, String addr, Date dob, char gen) {
        int gender;
        if (gen == 'm' || gen == 'M') {
            gender = 0;
        } else {
            gender = 1;
        }
        // the query
        try {
           askUpdate("INSERT INTO Administrative_data.patients(name, address, DOB, gender) " +
                    " Values (\"" + name + "\",\"" + addr + "\", \"" + dob + "\", " + gender + ") ");
          statement.close();
            ResultSet resultSet = askQuery("SELECT LAST_INSERT_ID() From Administrative_data.Patients");
            int patient_id=-1;
           if(resultSet.next())
           {
               patient_id=resultSet.getInt(1);
           }
            resultSet.close();
            statement.close();
            // add return statement
            return patient_id;
        } catch (Exception e) {
            System.out.println(e);

        }
        return -1;
    }

    /**
     * 
     * @param patient_id of the patient
     * @param service_id of the test that the patient wants to conduct
     * @param req_date   the date of preference
     * @return the appointment ID for further updates regarding the appointment
     */
    public int makeAppointment(int patient_id, int service_id, Date req_date) {
        try {
		checkService(service_id);
		checkPatient(patient_id);
         askUpdate("INSERT INTO ADMINISTRATIVE_DATA.Appointments(Patient_id, req_service_id, requested_date) " +
                    "Values(" + patient_id + "," + service_id + ",\"" + req_date + "\")"
                   );
            ResultSet resultSet= askQuery("SELECT LAST_INSERT_ID() from Administrative_data.Appointments");
            int order_id=-1;
            if(resultSet.next())
            {
            order_id = resultSet.getInt(1);
            }
            resultSet.close();
            statement.close();
            return order_id;
        } catch (Exception e) {
            System.out.println(e);
        }
        return -1;
    }

    /**
     * 
     * @param aid the request id for the appointment to retrive
     * @return a string representation of the appointment
     */
    public String getAppointment(int aid) throws Exception {
        String returnString = "";
        try {
	checkAppointment(aid);
            ResultSet resultSet = askQuery("SELECT * FROM ADMINISTRATIVE_DATA.Appointments WHERE request_id = " + aid);
            int i = 0;
            while (resultSet.next()) {
                i++;
                returnString += "patient_id:" + resultSet.getInt("patient_id") + "\t" + "service_id:"
                        + resultSet.getInt("req_service_id") + "\t" + "date:" + resultSet.getDate("requested_date");
            }
            if (i != 1) {
                throw new Exception("Could not find the requested appointment");
            }
            resultSet.close();
            statement.close();
        } catch (Exception e) {
            throw e;
        }
        return returnString;
    }

    /**
     * 
     * @param patient_id the patient_id of the patient making the cancellation
     * @param request_id the appointment number of the appointment to be cancelled
     * @return 1 if successful, 0 if not
     */
    public int deleteAppointment(int patient_id, int request_id)
            throws Exception {
        try {
	checkPatient(patient_id);
	checkAppointment(request_id);
         askUpdate("DELETE FROM ADMINISTRATIVE_DATA.Appointments"
                    + " WHERE request_id = " + request_id );
                    statement.close();
            return 1;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * On the date of the appointment, it is moved from the Appointment table to the
     * order_history table
     * this function provides the transfer functionality
     * 
     * @param patient_id of the concerned patient
     * @param request_id of the appointment that is being delivered
     * @return the order number of the appointment that was just executed
     */
    public int complete_appointment(int patient_id, int request_id)
            throws Exception {
        try {
            ResultSet resultSet = askQuery("SELECT * FROM ADMINISTRATIVE_DATA.APPOINTMENTS WHERE " +
                    " request_id = " + request_id);
            int service_id = 0;
            float unit_price = 0;
            Date date = null;
            while (resultSet.next()) {
                service_id = resultSet.getInt("req_service_id");
                date = resultSet.getDate("requested_date");
            }
            resultSet.close();
            statement.close();
            resultSet = null;
            resultSet = askQuery("SELECT * FROM ADMINISTRATIVE_DATA.Services WHERE service_id = " + service_id);// get service id info
            while (resultSet.next()) {
                // get unit price
                unit_price = resultSet.getFloat("unit_price");
            }
            resultSet.close();
            statement.close();
            resultSet = null;
            askUpdate("INSERT INTO ADMINISTRATIVE_DATA.Order_History(Patient_id, service_id,Unit_price,Date_of_appointment) " +
                    " Values( " + patient_id + " , " + service_id + " , " + unit_price + " ,\"" + date + "\")"
                  );
                  statement.close();
                  resultSet=askQuery("SELECT LAST_INSERT_ID() From Administrative_data.Order_history");
            int returnVal=-1;
            if(resultSet.next()){
            returnVal = resultSet.getInt(1);}
            resultSet.close();
            statement.close();
            deleteAppointment(patient_id, request_id);
            return returnVal;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Scans through the order history and calculates pending balance
     * 
     * @param patient_id of the patient
     * @return the entire pending balance
     */
    public float getBalance(int patient_id) throws Exception {
        try {
	checkPatient(patient_id);
            float balance = 0;
            ResultSet resultSet = askQuery("Select unit_price, amount_paid from ADMINISTRATIVE_DATA.Order_history " +
                    "where patient_id = " + patient_id);
            while (resultSet.next()) {
                balance += resultSet.getFloat("unit_price") - resultSet.getFloat("amount_paid");
            }
            resultSet.close();
            statement.close();
            return balance;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * clears the entire balance due for any given patient so that their effective
     * balance becomes 0
     * 
     * @param pid the patient id
     */
    public void clearDues(int pid) throws Exception {
        try {
	checkPatient(pid);
         askUpdate("Update ADMINISTRATIVE_DATA.Order_history " +
                    " SET amount_paid= unit_price " +
                    " Where patient_id = " + pid);
                    statement.close();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * generates invoice
     * 
     * @param pid patient of concern
     * @return a string representation of the invoice
     */
    public String genInvoice(int pid) throws Exception {
        try {
	checkPatient(pid);
            String outputString = "";
            ResultSet resultSet = askQuery(
                    "Select o.patient_id, s.name, o.unit_price, o.order_id, o.unit_price, o.amount_paid - o.unit_price as balance "
                            +
                            " from ADMINISTRATIVE_DATA.order_history o " +
                            " Join ADMINISTRATIVE_DATA.Services s On o.service_id = s.service_id " +
                            " Where o.patient_id = " + pid + " AND o.unit_price > o.amount_paid " );
            while (resultSet.next()) {
                outputString += ("Order id: "+resultSet.getInt("order_id") + "\t Name:" + resultSet.getString("name") + "\t Balance: "
                        + resultSet.getFloat("balance") + "\n");
                        
            }
            resultSet.close();
            statement.close();
            return outputString;

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Settles the amount for a particular order
     * 
     * @param pid      the patient id
     * @param order_id the order id to settle
     */
    public void clearBal(int pid, int order_id) throws Exception {
        try {
	checkPatient(pid);
            askUpdate("Update ADMINISTRATIVE_DATA.order_history " +
                    " Set amount_paid = unit_price " +
                    " where order_id = " + order_id);
                    statement.close();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 
     * @return a string containing all the available services at the test center
     */
    public String getServices() throws Exception {
        String outputString = "";
        try {
            ResultSet resultSet = askQuery(" select * from ADMINISTRATIVE_DATA.Services ");
            while (resultSet.next()) {
                int service_id = resultSet.getInt("service_id");
                String title = resultSet.getString("name").trim();
                int unit_price = resultSet.getInt("unit_price");
                outputString += "service_id: " + service_id + " name:" + title + " unit_price: " + unit_price + "\n";
            }
            resultSet.close();
            statement.close();
            return outputString;

        } catch (Exception e) {
            throw e;

        }
    }
    /**
     * 
     * @param pid the patient_id
     * @return a string representing the appointments made by particular patient
     * @throws Exception
     */
    public String viewAppointments(int pid) throws Exception
    {
checkPatient(pid);
        ResultSet resultSet= askQuery("SELECT * FROM ADMINISTRATIVE_DATA.Appointments A "+
        "JOIN ADMINISTRATIVE_DATA.SERVICES S "+
        "ON A.REQ_Service_ID = S.SERVICE_ID"+
        " WHERE A.Patient_id = "+ pid);
        String returnString="";
        while(resultSet.next())
        {
            returnString+=("Appointment_id: "+resultSet.getInt("request_id")+"\t Date: "+ resultSet.getDate("requested_date")+"\t Service_name:"+ resultSet.getString("name")+ "\n");
        }
        resultSet.close();
        statement.close();
        return returnString;
    }
    /**
     * to be used by an administrator to add services to the list of offerings 
     * @param sname - the name of the service to be added to the list of services 
     * @param uprice - the unit price of the same 
     */
    public void addService( String sname, int uprice) throws Exception
    {
        askUpdate("INSERT INTO ADMINISTRATIVE_DATA.SERVICES(name, unit_price) "+
        "values (\""+sname+"\","+ uprice+")");
        statement.close();
    }
    /** administrator function to update the unit price of a service 
     * 
     * @param sid this is the service id concerned
     * @param new_uprice this is the new unit_price
     */
    public void changeUprice(int sid, int new_uprice) throws Exception
    {
        askUpdate("UPDATE ADMINISTRATIVE_DATA.SERVICES "+
        " SET unit_price = "+new_uprice +
        " WHERE service_id = "+ sid );
        statement.close();
    }
    public void removeService(int sid) throws Exception
    {
        askUpdate("DELETE FROM ADMINISTRATIVE_DATA.SERVICES "+
        "WHERE SERVICE_ID = "+sid);
        statement.close();
    }
}
