package sample;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

public class Dictionary {
    public Vector<Word> words;

    public Dictionary() {
        words = new Vector<Word>();
    }

    // Trả về vị trí của từ có target là word
    public int indexOfWord (Word word) {
        System.out.println(words.size());
        int dictionaryLen = words.size();
        if (dictionaryLen == 0) return -1;
        int l = 0, r = dictionaryLen;

        while (l < r) {

            int mid = (l + r) >> 1;
            Word target = words.get(mid);
            System.out.println("mid = " + mid + "->>" + word.sameTargetWord(target));

            if (word.sameTargetWord(target)) return mid;
            System.out.println("oke go");
            if (word.isGreaterThan(target)) {
                l = mid + 1;
            } else {
                r = mid;
            }
        }

        if (l >= dictionaryLen || !words.get(l).sameTargetWord(word)) return -1;
        return l;
    }

    public int indexToInsert (Word word) {
        int dictionaryLen = words.size();
        if (dictionaryLen == 0) return -1;

        int l = 0, r = dictionaryLen;
        while (l < r) {
            int mid = (l + r) >> 1;
            Word target = words.get(mid);
            if (word.isGreaterThan(target)) {
                l = mid+1;
            } else {
                r = mid;
            }
        }
        return l;
    }

}
