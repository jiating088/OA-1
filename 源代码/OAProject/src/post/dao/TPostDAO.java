package post.dao;

import java.io.IOException;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import post.inf.PostInf;
import post.pojo.TPost;

/**
 * A data access object (DAO) providing persistence and search support for TPost
 * entities. Transaction control of the save(), update() and delete() operations
 * can directly support Spring container-managed transactions or they can be
 * augmented to handle user-managed Spring transactions. Each of these methods
 * provides additional information for how to configure it for the desired type
 * of transaction control.
 * 
 * @see post.pojo.TPost
 * @author MyEclipse Persistence Tools
 */

public class TPostDAO extends HibernateDaoSupport implements PostInf {
	private static final Logger log = LoggerFactory.getLogger(TPostDAO.class);
	// property constants
	public static final String SUSER = "suser";
	public static final String STITLE = "stitle";
	public static final String SCONTENT = "scontent";
	public static final String NSTATUS = "nstatus";
	public static final String NISFILE = "nisfile";
	public static final String UPDATEUSER = "updateuser";

	protected void initDao() {
		// do nothing
	}

	public void saveOrUpdate(TPost post) {

		log.debug("saving TPost instance");
		try {

			getHibernateTemplate().saveOrUpdate(post);

			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}

	}
	public void saveOrUpdate(TPost post,String content) {

		log.debug("saving TPost instance");
		try {
			getHibernateTemplate().saveOrUpdate(post);
			getHibernateTemplate().flush();
			getHibernateTemplate().refresh(post, LockMode.UPGRADE);
			// 字符流
			Clob clob = post.getScontent();
			Writer writer = null;
			try {
				writer = clob.setCharacterStream(1);
				writer.write(content);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			getHibernateTemplate().saveOrUpdate(post);
			
			log.debug("save successful");
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}

			
		
	}

	public int save(TPost post, String content) {

		log.debug("saving TPost instance");
		try {

			getHibernateTemplate().save(post);
			getHibernateTemplate().flush();

			getHibernateTemplate().refresh(post, LockMode.UPGRADE);
			// 字符流
			Clob clob = post.getScontent();
			Writer writer = null;
			try {
				writer = clob.setCharacterStream(0);
				writer.write(content);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			getHibernateTemplate().save(post);

			// 获得id
			String hql = "select id from TPost order by id";

			List<Integer> ids = getHibernateTemplate().find(hql);

			log.debug("save successful");
			return (ids.get(ids.size() - 1));
		} catch (RuntimeException re) {
			log.error("save failed", re);
			throw re;
		}

	}

	public void delete(TPost persistentInstance) {
		log.debug("deleting TPost instance");
		try {
			getHibernateTemplate().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public TPost findById(java.lang.Integer id) {
		log.debug("getting TPost instance with id: " + id);
		try {
			TPost instance = (TPost) getHibernateTemplate().get(
					"post.pojo.TPost", id);

			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(TPost instance) {
		log.debug("finding TPost instance by example");
		try {
			List results = getHibernateTemplate().findByExample(instance);
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding TPost instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from TPost as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findByPropertyDESC(String propertyName, Object value) {
		log.debug("finding TPost instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from TPost where "
					+ propertyName
					+ "="
					+ value
					+ " order by NVL(addtime, to_date('2000-01-01','yyyy-MM-dd'))DESC";
			System.out.println(queryString);
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findBystitledate(String stitle,String nowdate, String begindate, String enddate ,String nstatus) {
		// select stitle ,begindate,
		// nvl(begindate,to_date('2000-01-01','yyyy-MM-dd')) from t_post t where
		// begindate>to_date('2013-8-16','yyyy-MM-dd') and
		// enddate<to_date('2013-8-20','yyyy-MM-dd') and stitle like '%ew%'
		try {
			String queryString = "from TPost where 1=1  ";
			if(nowdate!=null &&!nowdate.equals("")){
				queryString=queryString+" and begindate<=to_date('"+nowdate+"','yyyy-MM-dd') ";
			}
			if(stitle!=null &&!stitle.equals("")){
				queryString=queryString+" and stitle like '%"+stitle+"%' ";
			}
			if(nstatus!=null &&!nstatus.equals("")){
				queryString=queryString+" and nstatus=1  ";
			}
			if(begindate!=null &&!begindate.equals("")){
				queryString=queryString+"and begindate>=to_date('"+begindate+"','yyyy-MM-dd') ";
			}
			if(enddate!=null &&!enddate.equals("")){
				queryString=queryString+"and enddate<=to_date('"+enddate+"','yyyy-MM-dd')";
			}
			queryString=queryString+" order by NVL(addtime, to_date('2000-01-01','yyyy-MM-dd'))DESC";
			System.out.println(queryString);
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List findBySuser(Object suser) {
		return findByProperty(SUSER, suser);
	}

	public List findByStitle(Object stitle) {
		return findByProperty(STITLE, stitle);
	}

	public List findByScontent(Object scontent) {
		return findByProperty(SCONTENT, scontent);
	}

	public List findByNstatus(Object nstatus) {
		return findByProperty(NSTATUS, nstatus);
	}

	public List findByNisfile(Object nisfile) {
		return findByProperty(NISFILE, nisfile);
	}

	public List findByUpdateuser(Object updateuser) {
		return findByProperty(UPDATEUSER, updateuser);
	}

	public List findAll() {
		log.debug("finding all TPost instances");
		try {

			String queryString = "from TPost";

			return getHibernateTemplate().find(queryString);

		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public TPost merge(TPost detachedInstance) {
		log.debug("merging TPost instance");
		try {
			TPost result = (TPost) getHibernateTemplate().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(TPost instance) {
		log.debug("attaching dirty TPost instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(TPost instance) {
		log.debug("attaching clean TPost instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public static TPostDAO getFromApplicationContext(ApplicationContext ctx) {
		return (TPostDAO) ctx.getBean("TPostDAO");
	}
}