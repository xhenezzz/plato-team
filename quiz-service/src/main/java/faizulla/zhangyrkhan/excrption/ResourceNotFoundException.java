package faizulla.zhangyrkhan.excrption;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s с ID %d не найден", resourceName, id));
    }
}