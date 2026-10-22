import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/ArithServlet"})
public class ArithServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String num1 = request.getParameter("txt1");
        String num2 = request.getParameter("txt2");
        String op = request.getParameter("math");
            
        int n1 = Integer.parseInt(num1);
        int n2 = Integer.parseInt(num2);
        PrintWriter pw = response.getWriter();
        
        try {
            switch(op) {
                case "+":
                    pw.println("Addition: " + (n1+n2));
                    break;

                case "-":
                    pw.println("Subtraction: " + (n1-n2));
                    break;

                case "*":
                    pw.println("Multiplication: " + (n1*n2));
                    break;

                case "/":
                    pw.println("Division: " + (n1/n2));
                    break;

                default:
                    pw.println("Invalid Operation");
                }
        }
        catch (ArithmeticException e) {
            pw.println("Cannot divide by zero");
        }
        
        
    }
}