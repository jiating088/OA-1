package user.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;

import pojo.TMenu;
import pojo.TUser;
import user.service.MenuService;
import user.service.UserService;

import com.opensymphony.xwork2.ModelDriven;


public class MenuAction extends BaseAction implements ModelDriven<TMenu>{
	private TMenu menu=new TMenu();
	@Override
	public TMenu getModel() {
		return menu;
	}

	private MenuService menuService;
	
	private UserService userService;
	private List<TMenu> menulist;
	private static String oldmenuname = null;
	public MenuService getMenuService() {
		return menuService;
	}
	public void setMenuService(MenuService menuService) {
		this.menuService = menuService;
	}
	public List<TMenu> getMenulist() {
		return menulist;
	}
	public void setMenulist(List<TMenu> menulist) {
		this.menulist = menulist;
	}
	public String getUserRoleMenu(){
		TUser nowUser=(TUser) getSession().getAttribute("user");
		menulist=userService.findUserRoleMenu(nowUser.getUserid());
		return "userrolemenu";		
	}
	/**
	 * 下移
	 * @return
	 */
	public String down(){
		menuService.down(menu);		
		this.message="操作成功";
		this.addDefaultURL("菜单列表", "menulist!view");
		return SUCCESS;
	}
	/**
	 * 上移
	 * @return
	 */
	public String up(){
		menuService.up(menu);		
		this.message="操作成功";
		this.addDefaultURL("菜单列表", "menulist!view");
		return SUCCESS;
	}

	/**
	 * 去添加菜单界面
	 * @return
	 */
	public String init(){
		menulist=menuService.seach();
		return "edit";
	}
	/**
	 * 保存或修改菜单
	 * @return
	 */
	public String saveOrUpdate(){
		TUser nowUser=(TUser) request.getSession().getAttribute("user");
		TUser user1 = userService.findByUserid(nowUser.getUserid());
        menu.setUpdateuser(user1.getUsername());
        menu.setUpdatetime(new Date());
		if ( menu.getMenuid() != null && menu.getMenuid()>0) {
			TMenu tmenu = menuService.findByMid(menu.getMenuid());
			menu.setIsdel(tmenu.getIsdel());
			menu.setOrderid(tmenu.getOrderid());
			this.menuService.update(menu);
			this.message="修改菜单成功";
			this.addDefaultURL("菜单列表", "menulist!view");
		}else{
			menu.setIsdel(0);
			menu.setAddtime(new Date());
			this.menuService.save(menu);
			this.message="新增菜单成功";
			this.addDefaultURL("菜单列表", "menulist!view");
			this.addURL("继续添加", "menu!init");
		}
		return SUCCESS;
	}
	/**
	 * 查看菜单名是否被使用
	 * @return
	 */
	public String checkMenuname(){
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out  = null;
		String lastName = null ;
		try {
			out= response.getWriter();
			lastName = new String(menu.getMenuname().getBytes("ISO-8859-1"), "GBK");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(!lastName.equals(oldmenuname)){
			boolean flag=menuService.checkMenuname(lastName,menu.getMenuid(),menu.getPid());
			if (flag) {
				// 存在
				out.print("1");
			} else {
				// 不存在
				out.print("0");
			}		
		}else{
			out.print("0");
		}
		oldmenuname = null;
		return null;
	}
	/**
	 * 去修改页面
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public String updatePage() throws IllegalAccessException, InvocationTargetException{
		TMenu menustr= this.menuService.findByMid(menu.getMenuid());
		oldmenuname = menustr.getMenuname();
		BeanUtils.copyProperties(menu, menustr);
		menulist=menuService.seach();
		return "edit";
	}
	public String delete(){
		boolean flag=menuService.isUse(menu.getMenuid());
		System.out.println(flag);
		if (!flag) {
			//未被使用
			this.menuService.delete(menu.getMenuid(),menu.getPid());
			this.message="删除菜单成功";
		}else {
			this.message="改菜单已被分配给某角色，不能删除";
		}		
		this.addDefaultURL("", "menulist!view");
		return SUCCESS;
	}
	public UserService getUserService() {
		return userService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	public String getOldmenuname() {
		return oldmenuname;
	}
	public void setOldmenuname(String oldmenuname) {
		this.oldmenuname = oldmenuname;
	}
	
	
}
