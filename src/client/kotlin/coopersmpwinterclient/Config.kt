package coopersmpwinterclient

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config

@Config(name = "coopersmpwinterclient")
class Config: ConfigData {

    var skipTextureVerification = false

}