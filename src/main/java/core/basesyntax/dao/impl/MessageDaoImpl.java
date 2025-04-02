package core.basesyntax.dao.impl;

import core.basesyntax.dao.MessageDao;
import core.basesyntax.exception.DataProcessingException;
import core.basesyntax.model.Message;
import core.basesyntax.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class MessageDaoImpl extends AbstractDao implements MessageDao {
    public MessageDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Message create(Message entity) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
            return entity;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't insert message: " + entity, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Message get(Long id) {
        Message message;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            message = session.get(Message.class, id);
            return message;
        } catch (Exception e) {
            throw new DataProcessingException("Can't get message by id: " + id, e);
        }
    }

    @Override
    public List<Message> getAll() {
        List<Message> messages;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            messages = session.createQuery("FROM Message", Message.class).getResultList();
        } catch (Exception e) {
            throw new DataProcessingException("Can't get all messages", e);
        }
        return messages;
    }

    @Override
    public void remove(Message message) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.remove(message);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't remove message", e);
        } finally {
            session.close();
        }
    }
}
