package core.inerface;

public interface IFilter<T> {

    ISelectQuery<T> apply(ISelectQuery<T> q);
}
