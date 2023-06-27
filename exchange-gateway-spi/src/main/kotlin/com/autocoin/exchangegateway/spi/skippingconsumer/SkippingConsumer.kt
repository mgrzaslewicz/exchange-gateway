package com.autocoin.exchangegateway.spi.skippingconsumer

/**
 * Use it to skip some jobs.
 */
interface SkippingConsumer {
    fun run(job: Runnable, onSkipped: Runnable = Runnable { })
}
