package frol;

public class AssetDto {
    private final String name;
    private final byte[] content;

    public AssetDto(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public String getPath() {
        return name;
    }

    public byte[] getContent() {
        return content;
    }
}
