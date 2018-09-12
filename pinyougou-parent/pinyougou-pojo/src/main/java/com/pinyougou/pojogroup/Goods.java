package com.pinyougou.pojogroup;

import java.io.Serializable;
import java.util.List;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;

public class Goods implements Serializable {
	private TbGoods tbGoods;  //spu
	private TbGoodsDesc tbGoodsDesc;//商品扩展
	private List<TbItem> itemList;//sku

	public TbGoods getTbGoods() {
		return tbGoods;
	}

	public void setTbGoods(TbGoods tbGoods) {
		this.tbGoods = tbGoods;
	}

	public TbGoodsDesc getTbGoodsDesc() {
		return tbGoodsDesc;
	}

	public void setTbGoodsDesc(TbGoodsDesc tbGoodsDesc) {
		this.tbGoodsDesc = tbGoodsDesc;
	}

	public List<TbItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<TbItem> itemList) {
		this.itemList = itemList;
	}

	@Override
	public String toString() {
		return "Goods [tbGoods=" + tbGoods + ", tbGoodsDesc=" + tbGoodsDesc + ", itemList=" + itemList + "]";
	}
	

}
