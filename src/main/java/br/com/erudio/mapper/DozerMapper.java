package br.com.erudio.mapper;

//import com.github.dozermapper.core.DozerBeanMapperBuilder;
//import com.github.dozermapper.core.Mapper;

import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

public class DozerMapper {

    //private static Mapper mapper = DozerBeanMapperBuilder.buildDefault();
    private static ModelMapper mapper = new ModelMapper();

    //Temos dois tipos, tipo origem O e o tipo destino D, que retorna D
    public static <O, D> D parseObject(O origin, Class<D> destination){
        return mapper.map(origin, destination);
    }

    public static <O, D> List<D> parseListObjects(List<O> origins, Class<D> destination){
        List<D> destinationObjects = new ArrayList<D>();

        for (O o : origins){
            destinationObjects.add(mapper.map(o, destination));
        }

        return destinationObjects;
    }

}
