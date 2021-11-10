package com.iteixeira.pocspringcloud

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@FeignClient(value = "customerBff", url = "\${customer-bff.url}")
interface CustomerBFFHttp {

    @PostMapping("\${customer-bff.endpoints.register-confirm}")
    fun confirm(customer:CustomerApi):String

}