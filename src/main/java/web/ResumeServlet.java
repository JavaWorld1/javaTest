package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Resume;
import storage.SqlStorage;
import exception.StorageException;

import java.io.IOException;
import java.io.InputStream;

@WebServlet("/resume")
public class ResumeServlet extends HttpServlet {
    private SqlStorage storage;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC Driver not found", e);
        }
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("resumes.properties")) {
            if (input == null) {
                throw new RuntimeException("resumes.properties not found in classpath");
            }
            storage = new SqlStorage();
        } catch (IOException e) {
            throw new RuntimeException("Error loading configuration file", e);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing SqlStorage", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String uuid = request.getParameter("uuid");
        String action = request.getParameter("action");

        if (action == null) {
            try {
                request.setAttribute("resumes", storage.getAllSorted());
                request.getRequestDispatcher("/WEB-INF/list.jsp").forward(request, response);
            } catch (StorageException e) {
                handleException(response, "Error retrieving all resumes", e);
            }
            return;
        }

        switch (action) {
            case "view":
                try {
                    Resume resume = storage.get(uuid);
                    request.setAttribute("resume", resume);
                    request.getRequestDispatcher("/WEB-INF/resume.jsp").forward(request, response);
                } catch (StorageException e) {
                    handleException(response, "Error retrieving resume", e);
                }
                break;

            case "delete":
                try {
                    storage.delete(uuid);
                    response.sendRedirect("resume");
                } catch (StorageException e) {
                    handleException(response, "Error deleting resume", e);
                }
                break;

            case "clear":
                try {
                    storage.clear();
                    response.sendRedirect("resume");
                } catch (StorageException e) {
                    handleException(response, "Error clearing storage", e);
                }
                break;

            default:
                response.sendRedirect("resume");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fullName = request.getParameter("fullName");

        if (fullName == null || fullName.trim().isEmpty()) {
            response.sendRedirect("resume");
            return;
        }

        Resume r = new Resume(fullName);
        try {
            storage.save(r);
            response.sendRedirect("resume");
        } catch (StorageException e) {
            handleException(response, "Error saving resume", e);
        }
    }

    private void handleException(HttpServletResponse response, String message, Exception e) throws IOException {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message + ": " + e.getMessage());
        e.printStackTrace();
    }
}
