package net.ccbluex.liquidbounce.features.command.commands

import me.kiras.aimwhere.utils.http.WebUtils
import net.ccbluex.liquidbounce.features.command.Command

class PhoneNumberCommand : Command("PhoneNumber", arrayOf("GetPhoneNumber")) {
    override fun execute(args: Array<String>) {
        Thread {
            if (args.size > 1) {
                val str = WebUtils.get("http://api.qb-api.com/qbtxt-api.php?qq=${args[1]}");
                if (str.contains("记录")) {
                    super.chat("库中没有这个记录！")
                    return@Thread
                }
                val str2 = str.split('\n');
                super.chat(
                    "手机号:${str2[1].split("mobile:")[1]} | 省:${str2[2].split("province:")[1]} | 市:${
                        str2[3].split(
                            "city:"
                        )[1]
                    }"
                )
                return@Thread
            }
            super.chatSyntax("PhoneNumber <QQ>")
        }.start()
    }
}