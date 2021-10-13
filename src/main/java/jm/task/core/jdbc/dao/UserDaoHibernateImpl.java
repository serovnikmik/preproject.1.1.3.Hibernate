package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.*;

import javax.persistence.EntityManager;
import java.util.List;

public class UserDaoHibernateImpl implements UserDao {

    private static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS user(" +
            "id BIGINT(1) AUTO_INCREMENT, " +
            "name VARCHAR(45) NOT NULL, " +
            "lastName VARCHAR(45) NOT NULL, " +
            "age TINYINT(1) NOT NULL, " +
            "PRIMARY KEY(id) " +
            ")";
    private static String DROP_TABLE = "DROP TABLE IF EXISTS user";
    private static String CLEAN_TABLE = "DELETE FROM user";

    private Session currentSession;
    private Transaction currentTransaction;

    public UserDaoHibernateImpl() {
    }

    public Session openCurrentSession(){
        currentSession = Util.getSessionFactory().openSession();
        return currentSession;
    }

    public Session openCurrentSessionWithTransaction(){
        currentSession = Util.getSessionFactory().openSession();
        currentTransaction = currentSession.beginTransaction();
        return currentSession;
    }

    public void closeCurrentSession(){
        currentSession.close();
    }

    public void closeCurrentSessionWithTransaction(){
        currentTransaction.commit();
        currentSession.close();
    }

    private static SessionFactory getSessionFactory(){
        return Util.getSessionFactory();
    }

    public Session getCurrentSession(){
        return currentSession;
    }

    @Override
    public void createUsersTable() {
        openCurrentSessionWithTransaction();
        SQLQuery query = currentSession.createSQLQuery(this.CREATE_TABLE);
        query.executeUpdate();
        closeCurrentSessionWithTransaction();
    }

    @Override
    public void dropUsersTable() {
        openCurrentSessionWithTransaction();
        SQLQuery query = currentSession.createSQLQuery(this.DROP_TABLE);
        query.executeUpdate();
        closeCurrentSessionWithTransaction();
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        openCurrentSessionWithTransaction();
        currentSession.save(new User(name, lastName, age));
        closeCurrentSessionWithTransaction();
    }

    @Override
    public void removeUserById(long id) {
        openCurrentSessionWithTransaction();
        User deletedUser = new User();
        deletedUser.setId(id);
        try{
            currentSession.delete(deletedUser);
        } catch (Error e){
            System.out.println("Что-то не так с удалением User-а");
        }
        closeCurrentSessionWithTransaction();
    }

    @Override
    public List<User> getAllUsers() {
//        String query = "select * from " + User.class.getSimpleName();
//        Session session = null;
//        try{
//            session = sessionFactory.openSession();
//            List<User> allUsers = (List<User>)session.createQuery(query).list();
//            session.close();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
        openCurrentSession();
        List<User> allUsers = currentSession.createCriteria(User.class).list();
        closeCurrentSession();
        return allUsers;
    }

    @Override
    public void cleanUsersTable() {
        openCurrentSessionWithTransaction();
        SQLQuery query = currentSession.createSQLQuery(this.CLEAN_TABLE);
        query.executeUpdate();
        closeCurrentSessionWithTransaction();
    }
}
