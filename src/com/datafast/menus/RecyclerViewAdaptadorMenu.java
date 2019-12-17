package com.datafast.menus;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.newpos.pay.R;
import com.datafast.definesDATAFAST.DefinesDATAFAST;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdaptadorMenu extends RecyclerView.Adapter<RecyclerViewAdaptadorMenu.ViewHolder> {

    public List<menuItemsModelo> menuItemsModeloList;
    Context context;
    String tipoLayout;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView TextoItemMenu;
        ImageView logoItem;
        List <menuItemsModelo> menuItems = new  ArrayList<menuItemsModelo>();
        Context ctx;
        String tipoLayout;

        public ViewHolder(View itemView, Context ctx, List<menuItemsModelo> menuItems, String tipoLayout) {
            super(itemView);
            this.ctx = ctx;
            this.menuItems = menuItems;
            this.tipoLayout = tipoLayout;
            itemView.setOnClickListener(this);
            if(tipoLayout.equals("LINEAR")){
                TextoItemMenu=(TextView)itemView.findViewById(R.id.tvItemMenuTrans);
                logoItem= (ImageView)itemView.findViewById(R.id.imgItemTrans);
            }
            else{
                TextoItemMenu=(TextView)itemView.findViewById(R.id.tvItemGrid);
                logoItem= (ImageView)itemView.findViewById(R.id.imgItemGrid);
            }

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            menuItemsModelo menuItemModelo= this.menuItems.get(position);
            MenuAction menuAction =  new MenuAction(ctx, menuItemModelo.getTextoItem());
            menuAction.SelectAction();
        }
    }


    public RecyclerViewAdaptadorMenu(List<menuItemsModelo> menuItemsModeloList, Context context, String tipoLayout) {
        this.menuItemsModeloList = menuItemsModeloList;
        this.context = context;
        this.tipoLayout = tipoLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int idItemMenu;

        if(tipoLayout.equals(DefinesDATAFAST.TIPO_LAYOUT_LINEAR)){
            idItemMenu = R.layout.itemmenu;

        }else{
           idItemMenu = R.layout.itemmenugrid;
        }

        View  view= LayoutInflater.from(parent.getContext()).inflate(idItemMenu, parent, false);

        ViewHolder viewHolder=new ViewHolder(view, context, menuItemsModeloList, tipoLayout);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.TextoItemMenu.setText(menuItemsModeloList.get(position).getTextoItem());
        holder.logoItem.setImageResource(menuItemsModeloList.get(position).getImgItemMenu());
    }

    @Override
    public int getItemCount() {
        return menuItemsModeloList.size();
    }
    
}

