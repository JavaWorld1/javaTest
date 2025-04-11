package com.webapp.web;


import com.webapp.model.Resume;
import com.webapp.storage.SqlStorage;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/resume")
public class ResumeServlet extends HttpServlet {
    String configPath = getServletContext().getRealPath("WEB-INF/config/resumes.properties");
    private final SqlStorage storage = new SqlStorage(configPath);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String uuid = request.getParameter("uuid");
        String action = request.getParameter("action");

        if (action == null) {
            request.setAttribute("resumes", storage.getAllSorted());
            request.getRequestDispatcher("/WEB-INF/jsp/list.jsp").forward(request, response);
            return;
        }

        switch (action) {
            case "view":
                Resume resume = storage.get(uuid);
                request.setAttribute("resume", resume);
                request.getRequestDispatcher("/WEB-INF/jsp/resume.jsp").forward(request, response);
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

