/*
 * Copyright (C) CredaRate Solutions GmbH
 */
package de.geburtig.lexer.util;

import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Implementierung eines generischen Iterators, der einen "Peek"-Mechanismus anbietet, also das Anschauen des nächsten,
 * übernächsten, vorherigen etc. Elements, ohne den Iterationszeiger zu ändern. Bietet zudem Methoden für die
 * Manipulation der darunterliegenden Collection.
 *
 * @param <E> Element type
 * @author jochen.geburtig
 */
@RequiredArgsConstructor
public class PeekableIterator<E> implements Iterator<E> {

    private final List<E> list;
    private int index = -1;

    @Override
    public boolean hasNext() {
        return list.size() > index + 1;
    }

    @Override
    public E next() {
        return list.get(++index);
    }

    @Override
    public void remove() {
        list.remove(index--);
    }

    /**
     * Setzt den Zeiger auf das nächste Element und entfernt dieses aus der Collection. Führt diese zwei Schritte so oft
     * aus, wie der übergebene Wert dies vorgibt.
     * @param amount Anzahl an Verarbeitungsschritten
     */
    public void removeNext(final int amount) {
        IntStream.range(0, amount).forEach(i -> {
            next();
            remove();
        });
    }

    /**
     * Liefert das nächste Element, also das was next() liefern würde, allerdings ohne, dass der Iterationszeiger
     * verschoben wird.
     * @return Next element
     */
    public E peekNext() {
        return list.get(index + 1);
    }

    public E peekNextPlus(final int offset) {
        return list.get(index + offset + 1);
    }

    public E prev() {
        return list.get(--index);
    }

    public E peekPrev() {
        return list.get(index - 1);
    }

    public E peekPrevPlus(final int offset) {
        return list.get(index - offset - 1);
    }

    public int elementsLeft() {
        return list.size() - (index + 1);
    }

    public void skip(final int offset) {
        index += offset;
    }

    public void replace(final E element) {
        list.set(index, element);
    }

    /**
     * Fügt ein Element an der aktuellen Stelle ein und rückt alle anderen Elemente einen Platz weiter nach hinten.
     * Der Iterator verhält sich so, als wäre das eingefügte Element mit dem letzten next() bezogen worden.
     * Der nächste Aufruf von next() liefert also das aktuelle, nach hinten geschobene Element, erneut.
     * @param element Element
     */
    public void insert(final E element) {
        list.add(index, element);
    }
}
