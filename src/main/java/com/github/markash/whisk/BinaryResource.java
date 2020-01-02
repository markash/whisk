package com.github.markash.whisk;

import java.io.File;
import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;

public class BinaryResource {
    private String id;
    private String fileName;
    private String data;
    private String link;
    private Long createTime;
    private Long updateTime;
    private Long deleteTime;
    private Long size;

    public BinaryResource() {}

    public BinaryResource(
        final JsonObject object) { 
        
        try {
            this.fileName = object.getString("fileName");
        } catch (Exception e) {
            throw new DecodeException("Unable to read fileName from JsonObject", e);
        }
        
        try {
            this.data = object.getString("data");
        } catch (Exception e) {
            throw new DecodeException("Unable to read data from JsonObject", e);
        }
    }

    /**
     * Loads the {@link BinaryResource} from the buffer
     * @throws NullPointerException if the buffer is null
     * @throws DecodeException if the buffer could not be decode to Json 
     */
    public static BinaryResource from(
        final Buffer buffer) {

        return new BinaryResource(new JsonObject(buffer));
    }

    public static BinaryResource withFileName(
        final File file) {

        return new BinaryResource()
            .withFileName(file.getName());
    }

    public String getId() { return id; }
    public void setId(final String id) { this.id = id; }

    public String getFileName() { return this.fileName; }
    public void setFileName(final String fileName) { this.fileName = fileName; }

    public String getData() { return this.data; }
    public void setData(String data) { this.data = data; }

    public String getLink() { return link; }
    public void setLink(final String link) { this.link = link; }

    public Long getCreateTime() { return this.createTime; }
    public void setCreateTime(final Long createTime) { this.createTime = createTime; }

    public Long getUpdateTime() { return this.updateTime; }
    public void setUpdateTime(final Long updateTime) { this.updateTime = updateTime; }

    public Long getDeleteTime() { return this.deleteTime; }
    public void setDeleteTime(final Long deleteTime) { this.deleteTime = deleteTime; }

    public Long getSize() { return this.size; }
    public void setSize(final Long size) { this.size = size; }

    public boolean hasBinaryData() {

        final String data = getData();
        return (data != null && !data.trim().isEmpty());
    }

    @JsonIgnore
    public byte[] getBinaryData() {

        if (hasBinaryData()) {
            return Base64.getDecoder().decode(getData());
        }
        return new byte[0];
    }

    public BinaryResource withFileName(
        final String fileName) {

        this.fileName = fileName;
        return this;
    }

    public BinaryResource withData(
        final byte[] data) {

        this.data = (data == null ? null : Base64.getEncoder().encodeToString(data));
        return this;
    }

    public BinaryResource withCreateTime(final Long createTime) {

        setCreateTime(createTime);
        return this;
    }

    public BinaryResource withUpdateTime(final Long updateTime) {

        setUpdateTime(updateTime);
        return this;
    }

    public BinaryResource withDeleteTime(final Long deleteTime) {

        setDeleteTime(deleteTime);
        return this;
    }

    public BinaryResource withId(final String id) {

        setId(id);
        return this;
    }

    public BinaryResource withLink(final String link) {

        setLink(link);
        return this;
    }

    public BinaryResource withSize(final Long size) {

        setSize(size);
        return this;
    }

}