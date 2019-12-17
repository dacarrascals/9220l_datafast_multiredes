package com.datafast.menus;

public class menuItemsModelo {

    private String textoItem;
    private int imgItemMenu;

    public menuItemsModelo() {
    }

    public menuItemsModelo(String textoItem, int imgItemMenu) {
        this.textoItem = textoItem;
        this.imgItemMenu = imgItemMenu;
    }

    public String getTextoItem() {
        return textoItem;
    }

    public void setTextoItem(String textoItem) {
        this.textoItem = textoItem;
    }

    public int getImgItemMenu() {
        return imgItemMenu;
    }

    public void setImgItemMenu(int imgItemMenu) {
        this.imgItemMenu = imgItemMenu;
    }
}
