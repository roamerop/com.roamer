package com.pinyougou.content.service.impl;

import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import com.util.mail.MailSenderInfo;
import com.util.mail.SimpleMailSender;

import entity.PageResult;

/**
 * 服务实现层
 * 
 * @author Administrator
 *
 */
@Service(timeout=5000)
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
		try {
			redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content) {
		// 删除之前广告
		Long id = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		redisTemplate.boundHashOps("content").delete(id);
		// 删除之后广告
		contentMapper.updateByPrimaryKey(content);
		// longvalue将包装类转化为基本数据类型
		if (id.longValue() != content.getCategoryId().longValue()) {
			redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		}
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id) {
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			try {
				redisTemplate.boundHashOps("content").delete(contentMapper.selectByPrimaryKey(id).getCategoryId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			contentMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbContentExample example = new TbContentExample();
		Criteria criteria = example.createCriteria();

		if (content != null) {
			if (content.getTitle() != null && content.getTitle().length() > 0) {
				criteria.andTitleLike("%" + content.getTitle() + "%");
			}
			if (content.getUrl() != null && content.getUrl().length() > 0) {
				criteria.andUrlLike("%" + content.getUrl() + "%");
			}
			if (content.getPic() != null && content.getPic().length() > 0) {
				criteria.andPicLike("%" + content.getPic() + "%");
			}
			if (content.getStatus() != null && content.getStatus().length() > 0) {
				criteria.andStatusLike("%" + content.getStatus() + "%");
			}

		}

		Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	// 查询展示广告
	// 加入了redis进行缓存,加入try catch不影响主程序运行
	@Override
	public List<TbContent> findByCategoryId(Long categoryId) {
		List<TbContent> list;
		try {
			list = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
			if (list != null) {
				System.out.println("缓存广告");
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

		// System.out.println(categoryId);
		TbContentExample example = new TbContentExample();
		// 根据sort_order排序,sql字段名称
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		criteria.andStatusEqualTo("1");
		example.setOrderByClause("sort_order");
		list = contentMapper.selectByExample(example);

		try {
			// 第一次查询
			redisTemplate.boundHashOps("content").put(categoryId, list);
			//int i = 1/0;
		} catch (Exception e) {
			e.printStackTrace();
			String message = e.getMessage();
			Scanner sc = new Scanner(System.in);
			String pass = sc.next();
			// 这个类主要是设置邮件
			MailSenderInfo mailInfo = new MailSenderInfo();
			mailInfo.setMailServerHost("smtp.163.com");
			mailInfo.setMailServerPort("25");
			mailInfo.setValidate(true);
			mailInfo.setUserName("18659108178@163.com");
			mailInfo.setPassword(pass);
			mailInfo.setFromAddress("18659108178@163.com");
			mailInfo.setToAddress("410735148@qq.com");
			mailInfo.setSubject("报警啦!!!");
			mailInfo.setContent(message);
			// 这个类主要来发送邮件
			SimpleMailSender sms = new SimpleMailSender();
			sms.sendTextMail(mailInfo);// 发送文体格式
			// sms.sendHtmlMail(mailInfo);// 发送html格式
			System.out.println("success");
		}
		System.out.println("数据库广告");
		return list;
	}

}
