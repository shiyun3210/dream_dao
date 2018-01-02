package org.lemur.service;

public class TableBean
{
  private String tableName;
  private String tableComment;

  public String getTableName()
  {
    return this.tableName;
  }
  public void setTableName(String tableName) {
    this.tableName = tableName;
  }
  public String getTableComment() {
    return this.tableComment;
  }
  public void setTableComment(String tableComment) {
    this.tableComment = tableComment;
  }
}