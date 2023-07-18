package net.eiradir.server.entity.network

interface NetworkDataProvider {
    fun getNetworkedData(): Map<NetworkedDataKey, String>
}