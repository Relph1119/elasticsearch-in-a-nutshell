package com.mingyi.cn;

import java.util.ArrayList;
import java.util.List;

public class Product {
    String msku;
    String mtype;
    double mprice;
    List<Product> m_product_list;

    Product(String pid, String ptype, double pprice) {
        msku = pid;
        mtype = ptype;
        mprice = pprice;
        m_product_list = new ArrayList<Product>();
    }

    Product() {
        m_product_list = new ArrayList<Product>();
    }

    public String getMsku() {
        return msku;
    }

    public void setMsku(String msku) {
        this.msku = msku;
    }

    public String getMtype() {
        return mtype;
    }

    public void setMtype(String mtype) {
        this.mtype = mtype;
    }

    public double getMprice() {
        return mprice;
    }

    public void setMprice(double mprice) {
        this.mprice = mprice;
    }

    public List<Product> getM_product_list() {
        return m_product_list;
    }

    public void setM_product_list(List<Product> m_product_list) {
        this.m_product_list = m_product_list;
    }

    public void bulkWriteProducts() {
        for (int i = 0; i < 100; i++) {
            String strid = "bk-" + i;
            String strtype = "City bike " + i;
            float fprice = (float) (Math.random() * 3 * 100);
            Product curpro = new Product(strid, strtype, fprice);
            //System.out.println(strid + " " + strtype + " " + fprice);
            m_product_list.add(curpro);
        }
    }

    public List<Product> fetchProducts() {
        // TODO Auto-generated method stub
        bulkWriteProducts();
        return m_product_list;
    }

}
