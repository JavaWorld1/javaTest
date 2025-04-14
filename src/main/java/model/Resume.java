package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * Initial resume class
 */

public class Resume implements Comparable<Resume>, Serializable {

    @Serial
    private static final long serialVersionUID = 5912822857433624320L;

    // Unique identifier
    private String uuid;

    private String fullName;

    private final Map<model.ContactType, String> contacts = new EnumMap<>(model.ContactType.class);
    private final Map<model.SectionType, model.Section> sections = new EnumMap<>(model.SectionType.class);

    public Resume(String fullName) {
        this(UUID.randomUUID().toString(), fullName);
    }

    public Resume(String uuid, String fullName) {
        Objects.requireNonNull(uuid, "uuid must not be null");
        Objects.requireNonNull(fullName, "fullName must not be null");
        this.uuid = uuid;
        this.fullName = fullName;
    }

    public Resume() {
    }

    public String getFullName() {
        return fullName;
    }

    public String getUuid() {
        return uuid;
    }

    public Map<model.ContactType, String> getContacts() {
        return contacts;
    }

    public Map<model.SectionType, model.Section> getSections() {
        return sections;
    }

    public String getContact(model.ContactType type) {
        return contacts.get(type);
    }

    public model.Section getSection(model.SectionType type) {
        return sections.get(type);
    }

    public void addContact(model.ContactType type, String value) {
        contacts.put(type, value);
    }

    public void addSection(model.SectionType type, model.Section section) {
        sections.put(type, section);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resume resume = (Resume) o;
        return Objects.equals(uuid, resume.uuid) &&
                Objects.equals(fullName, resume.fullName) &&
                Objects.equals(contacts, resume.contacts) &&
                Objects.equals(sections, resume.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, fullName, contacts, sections);
    }

    @Override
    public String toString() {
        return "Resume{" +
                "uuid='" + uuid + '\'' +
                ", fullName='" + fullName + '\'' +
                ", contacts=" + contacts +
                ", sections=" + sections +
                '}';
    }

    @Override
    public int compareTo(Resume o) {
        int cmp = fullName.compareTo(o.fullName);
        return cmp != 0 ? cmp : uuid.compareTo(o.uuid);
    }

    public void debugPrint() {
        System.out.println("Resume: " + fullName + " (" + uuid + ")");
        System.out.println("Contacts:");
        for (Map.Entry<model.ContactType, String> entry : contacts.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("Sections:");
        for (Map.Entry<model.SectionType, model.Section> entry : sections.entrySet()) {
            System.out.println("  " + entry.getKey() + ":");

            model.Section section = entry.getValue();

            if (section instanceof model.OrganizationSection) {
                List<model.Organization> orgs = ((model.OrganizationSection) section).getOrganizations();
                if (orgs == null) {
                    System.out.println("    [WARNING] Organizations list is null!");
                    continue;
                }
                for (model.Organization org : orgs) {
                    System.out.println("    Organization: " + org);
                    if (org.getPositions() == null) {
                        System.out.println("      [WARNING] Positions list is null!");
                        continue;
                    }
                    for (model.Organization.Position pos : org.getPositions()) {
                        System.out.println("      Position: " + pos);
                    }
                }
            } else {
                System.out.println("    " + section);
            }
        }
    }
}
