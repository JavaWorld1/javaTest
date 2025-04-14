package web;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Resume;
import storage.SqlStorage;

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
            throw new RuntimeException(e);
        }
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("resumes.properties")) {
            if (input == null) {
                throw new RuntimeException("resumes.properties не найден в classpath");
            }
            storage = new SqlStorage(input);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки файла конфигурации", e);
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
                Resume resume = storage.get(uuid);
                request.setAttribute("resume", resume);
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
        storage.save(r);
        response.sendRedirect("resume");
    }
}

