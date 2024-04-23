package br.com.erudio.integrationtests.vo.wrappers;

import br.com.erudio.integrationtests.vo.BookVO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@XmlRootElement
public class BookEmbeddedVO implements Serializable {

    private static final long serialVersioUID = 1L;

    @JsonProperty("bookVOList")
    private List<BookVO> book;

    public BookEmbeddedVO() {
    }

    public List<BookVO> getBook() {
        return book;
    }

    public void setBook(List<BookVO> book) {
        this.book = book;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookEmbeddedVO that = (BookEmbeddedVO) o;
        return Objects.equals(book, that.book);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(book);
    }
}
