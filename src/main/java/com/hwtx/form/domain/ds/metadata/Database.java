
package com.hwtx.form.domain.ds.metadata;

import java.io.Serializable;

public class Database extends BaseMetadata<Database> implements Serializable {
    protected String charset; // 编码
    protected String collate; // 排序编码
    protected String filePath; // 文件位置
    protected String logPath; // 日志位置

    public Database() {
    }

    public Database(String name) {
        setName(name);
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getCollate() {
        return collate;
    }

    public void setCollate(String collate) {
        this.collate = collate;
    }

    public String toString() {
        return name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }
}
