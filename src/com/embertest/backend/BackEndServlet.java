package com.embertest.backend;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * Servlet implementation class BackEndServlet
 */
@WebServlet("/emberbackend/*")
public class BackEndServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public BackEndServlet() {
        // TODO Auto-generated constructor stub
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(!validateRequest(request, response)){
			return;
		}
		
		String contactId=parseContactId(request);
		if(contactId==null){
			sendResponse(toJSON(getContacts(request)), response);
		} else{
			Contact c=getContact(request,contactId);
			if(c!=null){
				sendResponse(toJSON(c), response);
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(!validateRequest(request, response)){
			return;
		}
		if(getContacts(request).size()<100){
			Contact c=parseContact(request);
			c.setId(System.currentTimeMillis());
			getContacts(request).add(c);
			sendResponse(toJSON(c), response);
		} else{
			sendError(response);
		}
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(!validateRequest(request, response)){
			return;
		}
		String contactId=parseContactId(request);
		Contact c = parseContact(request);
		updateContact(request, contactId, c);
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(!validateRequest(request, response)){
			return;
		}
		String contactId=parseContactId(request);
		deleteContact(request, contactId);
	}
	
	private String parseContactId(HttpServletRequest request){
		String [] parts=request.getPathInfo().substring(1).split("/");
		if(parts.length==2){
			return parts[1];
		} else{
			return null;
		}
	}
	
	private Contact getContact(HttpServletRequest request, String contactId){
		for(Contact c : getContacts(request)){
			if(c.getId()==Long.parseLong(contactId)){
				return c;
			}
		}
		return null;
	}
	
	private Contact parseContact(HttpServletRequest request) throws IOException{
		ServletInputStream in=request.getInputStream();
		InputStreamReader reader=new InputStreamReader(in);
		Gson gson=new Gson();
		Contact c = gson.fromJson(reader, Contact.class);
		reader.close();
		return c;
	}
	
	private void updateContact(HttpServletRequest request,String contactId, Contact information){
		Contact c=getContact(request, contactId);
		if(c!=null){
			c.setFirstName(information.getFirstName());
			c.setLastName(information.getLastName());
			c.setPhone(information.getPhone());
			c.setEmail(information.getEmail());
		}
	}
	
	private void deleteContact(HttpServletRequest request,String contactId){
		Contact c=getContact(request, contactId);
		if(c!=null){
			getContacts(request).remove(c);
		}
	}
	
	private List<Contact> getContacts(HttpServletRequest request){
		List<Contact> contacts=(List<Contact>) request.getSession().getAttribute("contacts");
		if(contacts==null){
			contacts=createDemoData();
			request.getSession().setAttribute("contacts", contacts);
		}
		return contacts;
	}
	
	private void sendResponse(String json, HttpServletResponse response) throws IOException{
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out=response.getWriter();
		out.print(json);
		out.flush();
	}

	private String toJSON(Contact c){
		Gson gson=new Gson();
	    JsonElement je = gson.toJsonTree(c);
		JsonObject jo = new JsonObject();
		jo.add("contact", je);
		return jo.toString();
	}
	
	private String toJSON(List<Contact> contacts) {
		Type listType=new TypeToken<List<Contact>>(){}.getType();
		Gson gson=new Gson();
	    JsonElement je = gson.toJsonTree(contacts, listType);
		JsonObject jo = new JsonObject();
		jo.add("contacts", je);
		return jo.toString();
	}

	private boolean validateRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String pathInfo=request.getPathInfo();
		if(pathInfo==null || pathInfo.length()==0 || !pathInfo.startsWith("/contacts")){
			System.out.println("invalid path info "+request.getPathInfo());
			sendError(response);
			return false;
		}
		return true;
	}
	
	private void sendError(HttpServletResponse response) throws IOException{
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		OutputStream out=response.getOutputStream();
		out.write("Invalid request".getBytes());
		out.flush();
		out.close();
	}
	
	private List<Contact> createDemoData(){
		List<Contact> contacts=new ArrayList<Contact>();
		Contact c=new Contact();
		c.setId(0);
		c.setFirstName("John");
		c.setLastName("Doe");
		c.setPhone("1234");
		c.setEmail("example@example.com");
		contacts.add(c);
		c=new Contact();
		c.setId(1);
		c.setFirstName("Jack");
		c.setLastName("Doe");
		c.setPhone("1235");
		c.setEmail("example2@example.com");
		contacts.add(c);
		return contacts;
	}
	
	
}
