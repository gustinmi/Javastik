package com.javastik.servlets;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/jdbcpool")
public class PoolServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		   response.setContentType("text/plain;charset=UTF-8");
           final PrintWriter out = response.getWriter();
           out.print("testing 123");
           //out.print(NavadnaBaza.instance.get());
           out.flush();
           return;
		
	}

}
