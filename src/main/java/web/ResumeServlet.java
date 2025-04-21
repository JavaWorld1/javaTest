package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Resume;
import storage.SqlStorage;

import java.io.IOException;

@WebServlet("/resume")
public class ResumeServlet extends HttpServlet {
    private SqlStorage storage;

    @Override
    public void init() {
        try {
            storage = new SqlStorage();
        } catch (Exception e) {
            throw new RuntimeException("Storage init error", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String uuid = request.getParameter("uuid");
        String action = request.getParameter("action");

        if (action == null) {
            request.setAttribute("resumes", storage.getAllSorted());
            request.getRequestDispatcher("/WEB-INF/list.jsp").forward(request, response);
            return;
        }

        switch (action) {
            case "view":
                request.setAttribute("resume", storage.get(uuid));
                request.getRequestDispatcher("/WEB-INF/resume.jsp").forward(request, response);
                break;
            case "delete":
                storage.delete(uuid);
                response.sendRedirect("resume");
                break;
            case "clear":
                storage.clear();
                response.sendRedirect("resume");
                break;
            default:
                response.sendRedirect("resume");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String fullName = request.getParameter("fullName");
        if (fullName != null && !fullName.trim().isEmpty()) {
            Resume r = new Resume(fullName.trim());
            storage.save(r);
        }
        response.sendRedirect("resume");
    }
}
