/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tictactoeclient.network.dtos;

/**
 *
 * @author yasse
 */
public class GameMoveDto {
    private int row ;
    private int column;
    
    public GameMoveDto(){
    }
    public GameMoveDto(int row,int column){
        this.row = row;
        this.column = column;
    }
    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }
    
    public int getCol() { return column; }
    public void setCol(int column) { this.column = column; }
}
