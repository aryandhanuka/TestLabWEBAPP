# TestLabWEBAPP
Technologies used: MySQL, Java Servlet, HTML, JS, CSS, Java 
The project was created to be uploaded on a Tomcat 9 servlet container
the project dependencies are as follows:
MySQL connector library <=DBconnection.class<=Patient.class<=HelloForm.class 
DBconnection.class and Patient.class were stored as BloodBank.jar and included in the class-path when compiling HelloForm.class
The project offers the following functionality:
1) users can register themselves as patients using their name, gender, birth date ,and address (the program checks if the birthdate is valid)
2) Users can book appointments for the different services offered by the lab=>here errors are shown in case of invalid arguments( bad patient id or bad service id)
3) Users can cancel appointments=> here errors are shown incase of invalid arguments( bad patient id or bad service id)
4) Users can view and settle their outstanding payments:there is one button that settles all outstanding payments at once, in phase 2 of this project there will be more functionality regarding the invoice page
Other information:
there is also a command line version of the patient interface.
there is another command line interface for administrator-level functions in the previous repository.
The administrator-level interface has the functionality to add services, remove them, change prices, troubleshoot in case of errors with the Database that need to be resolved.

