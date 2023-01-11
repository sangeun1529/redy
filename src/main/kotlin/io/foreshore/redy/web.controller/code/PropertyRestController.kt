package io.foreshore.redy.web.controller.code

import com.obvioustraverse.miska.util.api.ApiReturn
import io.foreshore.redy.service.code.PropertyDto
import io.foreshore.redy.service.code.PropertyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.Size

@RestController
@RequestMapping("/ajax/property")
@PreAuthorize("hasAuthority('SYS_CODE')")
class PropertyRestController(
    @Autowired private val propertyService: PropertyService
) {
    @GetMapping("/")
    fun list(@Valid @Size(max = 200) @RequestParam("search") search: String?, pageable: Pageable) =
        ApiReturn.of(propertyService.findAll(search, pageable))

    @PostMapping("/")
    fun create(@RequestBody @Valid request: PropertyDto) = ApiReturn.of(propertyService.create(request))

    @PutMapping("/{key}")
    fun update(
        @Valid @Size(min = 1, max = 50) @PathVariable("key") key: String,
        @RequestBody @Valid request: PropertyDto
    ) = ApiReturn.of(propertyService.update(request.apply { this.key = key }))

    @DeleteMapping("/{key}")
    fun delete(@Valid @Size(min = 1, max = 50) @PathVariable("key") key: String) = key.run { propertyService.delete(this); ApiReturn.OK }

    @GetMapping("/{key}")
    fun get(@Valid @Size(min = 1, max = 50) @PathVariable("key") key: String) = ApiReturn.of(PropertyDto(propertyService.findByIdOrElseThrow(key)))
}
