/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pspcl;

/**
 *
 * @author amandeep
 */
public class file_detail {
    int billing_group,billing_cycle;
    String date;

    public file_detail(int billing_group,int billing_cycle,String date) {
        this.billing_cycle=billing_cycle;
        this.billing_group=billing_group;
        this.date=date;
    }
 
}
