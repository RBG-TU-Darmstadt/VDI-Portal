package vdi.webinterface.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.resteasy.client.ProxyFactory;

import vdi.commons.common.Configuration;
import vdi.commons.common.RESTEasyClientExecutor;
import vdi.commons.web.rest.interfaces.ManagementVMService;

/**
 * Servlet implementation class Home.
 */
public class Screenshot extends HttpServlet {

	private static final long serialVersionUID = 1L;

	ManagementVMService mangementVMService;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Screenshot() {
		super();

		mangementVMService = ProxyFactory.create(ManagementVMService.class,
				Configuration.getProperty("managementserver.uri") + "/vm/",
				RESTEasyClientExecutor.get());
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		/*
		 * TODO: Use tudUserUniqueID from SSO
		 */
		String userId = "123456";

		// Get request parameters
		long id = Long.valueOf(request.getParameter("machine"));
		int width = Integer.valueOf(request.getParameter("width"));
		int height = Integer.valueOf(request.getParameter("height"));

		byte[] screenshot = mangementVMService.getMachineScreenshot(userId, id, width, height);

		if (screenshot == null) {
			// Image not found
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		// Set content type
		response.setContentType("image/png");

		OutputStream output = response.getOutputStream();

		// Copy the image to the output stream
		output.write(screenshot);

		output.close();
	}

}
