package io.lipinski.board.engine;

import com.google.common.collect.ImmutableList;
import io.lipinski.board.Direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class Move {

    private List<Direction> directions;


    public Move(List<Direction> directions) {
        Direction[] arr = new Direction[directions.size()];
        for (int i = 0; i < directions.size(); i++) {
            arr[i] = directions.get(i);
        }
        this.directions = List.of(arr);
    }

    Stream<Direction> stream() {
        return this.directions.stream();
    }


    public List<Direction> getMove() {
        List<String> all = new ArrayList<>();
        all.add("Original");
        List<String> coped = new ArrayList<>(all);
        Collections.copy(coped, all);
        coped.forEach(System.out::println);
        all.set(0, "Haha");
        System.out.println(all.get(0));
        System.out.println("Is ok? " + coped.get(0));
        return ImmutableList.copyOf(directions);
    }


}