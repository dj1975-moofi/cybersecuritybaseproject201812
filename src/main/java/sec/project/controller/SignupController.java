package sec.project.controller;

/* This controller is used for all functionality.
   It was coded by the lowest bidder. Someone said it is secure since
   http sessions are used.

   Each URL / Function is described below.
*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class SignupController {

    /* So now we use sessions and thus we are secure.*/
    @Autowired
    private HttpSession session;
    
    @RequestMapping("/")
    public String defaultMapping() {
        // Just a redirect to the registration page.
        return "redirect:/form";
    }

    @RequestMapping("/0")
    public String userId0Mapping() {
        // This is basically used for both "no such user" as well as wrong password
        return "redirect:/login";
    }
    
    
    @RequestMapping(value = "/loggedin", method = RequestMethod.POST)
    public String newPassword(@RequestParam String password, @RequestParam Integer id) throws Exception {
        
        // This is used for changing the password
        // SQL Injection possible
        Connection connection = DriverManager.getConnection("jdbc:h2:file:./database", "sa", "");
        String SQLQuery = String.format("UPDATE Users SET password='%s' WHERE id = '%s'",password,Integer.toString(id));  
        connection.createStatement().executeUpdate(SQLQuery);
        connection.close();
        return "redirect:/" + Integer.toString(id);
    }
        

    @RequestMapping(value = {"/secret-debug", "/secret-debug/{command}"}, method = RequestMethod.GET)
    public String dumpDB(@PathVariable Optional<String> command) throws Exception {
        //This just dumps the database on each load.
        //For simple debugging, and since the url is secret it is no security problem.
        //More functionality, call with specific command and it will be executed
        //But this is not yet implemented.
        Connection nconnection = DriverManager.getConnection("jdbc:h2:file:./database", "sa", "");
        ResultSet nresultSet = nconnection.createStatement().executeQuery("SELECT * FROM Users;");
        while (nresultSet.next()) {
            String nid = nresultSet.getString("id");
            String nname = nresultSet.getString("name");
            String npassword = nresultSet.getString("password");
            System.out.println(nid + "\t" + nname + "\t" + npassword);
        }
        nresultSet.close();
        nconnection.close();
        return "redirect:/form";
    }

    @RequestMapping("/{id}")
    public String loadUser(Model model, @PathVariable Integer id) throws Exception {

        // User is redirected here after logging in

        Connection connection = DriverManager.getConnection("jdbc:h2:file:./database", "sa", "");
        String SQLQuery = String.format("SELECT * FROM Users where id='%d'",id);  
        ResultSet resultSet = connection.createStatement().executeQuery(SQLQuery);
        // assuming each email address is unique = single id comes back or nothing at all
        resultSet.next();
        
        // We pass the ID to the client, to use it later when changing password
        // "easier that way"
        model.addAttribute("id",resultSet.getInt("id"));
        model.addAttribute("name",resultSet.getString("name"));
        model.addAttribute("address",resultSet.getString("address"));
        model.addAttribute("email",resultSet.getString("email"));
        model.addAttribute("password",resultSet.getString("password"));
        
        resultSet.close();
        connection.close();
        session.setAttribute("loggedin", true);
        return "loggedin";
    } 

    

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String submitLogin(@RequestParam String email, @RequestParam String password) throws Exception {

        // This is where the user authenticates.
        
        // Classic SQL Injection possible during auth
        Connection connection = DriverManager.getConnection("jdbc:h2:file:./database", "sa", "");
        String SQLQuery = String.format("SELECT id FROM Users WHERE email = '%s' AND password = '%s'",email,password);  
        ResultSet resultSet = connection.createStatement().executeQuery(SQLQuery);

        // assuming each email address is unique = single id comes back or nothing at all
        // if nothing comes back, the user id is 0 which is mapped to a redirect back to the form.
        Integer id = 0;
        if (resultSet.next()) {
            id = resultSet.getInt("id");
        }
        resultSet.close();
        connection.close();
        return "redirect:/" + Integer.toString(id);
    } 

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(@RequestParam String name, @RequestParam String address, @RequestParam String email, @RequestParam String password) throws Exception {
         // SQL Injection possible here,
         // but its easier
        Connection connection = DriverManager.getConnection("jdbc:h2:file:./database", "sa", "");
        String SQLQuery = String.format("INSERT INTO Users (name,address,email,password) VALUES('%s','%s','%s','%s')",name,address,email,password);  
        connection.createStatement().executeUpdate(SQLQuery);
        connection.close();
        return "done";
    }
    
    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loadLogin() {
        return "login";
    } 

}
