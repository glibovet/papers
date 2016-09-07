package ua.com.papers.pojo.view;


import ua.com.papers.pojo.enums.RolesEnum;

/**
 * Created by oleh_kurpiak on 07.09.2016.
 */
public class UserView {

    private int id;

    private String email;

    private String password;

    private String name;

    private Boolean active;

    private RolesEnum role;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public RolesEnum getRole() {
        return role;
    }

    public void setRole(RolesEnum role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserView userView = (UserView) o;

        if (id != userView.id) return false;
        if (email != null ? !email.equals(userView.email) : userView.email != null) return false;
        if (password != null ? !password.equals(userView.password) : userView.password != null) return false;
        if (name != null ? !name.equals(userView.name) : userView.name != null) return false;
        if (active != null ? !active.equals(userView.active) : userView.active != null) return false;
        return role == userView.role;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (active != null ? active.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserView{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", role=" + role +
                '}';
    }
}
