import java.sql.Date;
public class Patient {
   int patient_id;
   Date dob;
   String addr;
   String name;
   char gender;// m or f
   static DBconnection db = new DBconnection();

   /**
    * 
    * @param name name of patient
    * @param addr addr of the patient
    * @param dob  date of birth of patient
    * @param gen  gender of the patient
    */
   public Patient(String name, String addr, Date dob, char gen) throws Exception {
      this.name = name;
      this.addr = addr;
      this.dob = dob;
      if (!(gen == 'm' || gen == 'M' || gen == 'F' || gen == 'f')) {
         throw new Exception();
      } else {
         int pid = db.registerPatient(name, addr, dob, gen);
         this.patient_id = pid;
      }
   }

   // make_appointment
   static int make_appointment(int pid, int sid, Date req_date) throws Exception {
      try {
         return db.makeAppointment(pid, sid, req_date);
      } catch (Exception e) {
         throw e;
      }
   }

   // delete_appointment
   static void delete_appointment(int pid, int rid) throws Exception {
      try {
         db.deleteAppointment(pid, rid);
      } catch (Exception e) {
         throw e;
      }
   }

   // get_balance
   static float get_balance(int pid) throws Exception {
      try {
         return db.getBalance(pid);
      } catch (Exception e) {
         throw e;
      }
   }

   // get_invoice
   static String get_invoice(int pid) throws Exception {
      try {
         return db.genInvoice(pid);
      } catch (Exception e) {
         throw e;
      }
   }

   static void pay_balance(int pid, int oid) throws Exception {
      try {
         db.clearBal(pid, oid);
      } catch (Exception e) {
         throw e;
      }
   }

   // check_offerings
   static String get_offerings() throws Exception {
      try {
         return db.getServices();
      } catch (Exception e) {
         throw e;
      }
   }

}
