/**
 * This class was created by <Vazkii>. It's distributed as
 * part of Better With Mods. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [26/03/2016, 21:31:04 (GMT)]
 */

package betterwithmods.core;

import java.util.HashMap;

public class ClassnameMap extends HashMap<String, String> {

    public ClassnameMap(String... s) {
        for(int i = 0; i < s.length / 2; i++)
            put(s[i * 2], s[i * 2 + 1]);
    }

    @Override
    public String put(String key, String value) {
        return super.put("L" + key + ";", "L" + value + ";");
    }

}