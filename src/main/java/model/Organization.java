package model;

import util.DateUtil;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Organization implements Serializable {
    @Serial
    private static final long serialVersionUID = -5257575292084614791L;
    private Link linkHomePage;
    private List<Position> positions = new ArrayList<>();

    public Organization(String name, String url, Position... positions) {
        this(new Link(name, url), Arrays.asList(positions));
    }

    public Organization(Link linkHomePage, List<Position> positions) {
        this.linkHomePage = linkHomePage;
        this.positions = positions;
    }

    public Organization() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization that = (Organization) o;
        return Objects.equals(linkHomePage, that.linkHomePage) &&
                Objects.equals(positions, that.positions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(linkHomePage, positions);
    }

    @Override
    public String toString() {
        return "Organization(" + linkHomePage + "," + positions + ')';
    }

    public List<Position> getPositions() {
        return positions;
    }

    public static class Position implements Serializable {
        @Serial
        private static final long serialVersionUID = 7848249962642648127L;

        private LocalDate startDate;
        private LocalDate endDate;

        private String title;
        private String description;

        public Position(int startYear, Month startMonth, String title, String description) { //работаю по текущий момент
            this(DateUtil.of(startYear, startMonth), DateUtil.NOW, title, description);
        }

        public Position(int startYear, Month startMonth, int endYear, Month endMonth, String title,
                        String description) {
            this(DateUtil.of(startYear, startMonth), DateUtil.of(endYear, endMonth), title, description); //startDate, endDate, ..., ...
        }

        public Position(LocalDate startDate, LocalDate endDate, String title, String description) {
            Objects.requireNonNull(startDate, "startDate must not be null");
            Objects.requireNonNull(endDate, "endDate must not be null");
            Objects.requireNonNull(title, "title must not be null");
            this.startDate = startDate;
            this.endDate = endDate;
            this.title = title;
            this.description = description;
        }

        public Position() {
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return Objects.equals(startDate, position.startDate) && Objects.equals(endDate, position.endDate) && Objects.equals(title, position.title) && Objects.equals(description, position.description);
        }

        @Override
        public int hashCode() {
            return Objects.hash(startDate, endDate, title, description);
        }

        @Override
        public String toString() {
            return "Position(" + startDate + ',' + endDate + ',' + title + ',' + description + ')';
        }
    }
}
