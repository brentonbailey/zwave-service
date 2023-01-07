package com.bbailey.smarthome.zwave.commandclass;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import com.bbailey.smarthome.zwave.commandclass.VersionCommandClass.VersionedCommandClass;
import com.bbailey.smarthome.zwave.device.ZwaveNode;
import com.bbailey.smarthome.zwave.utils.BitUtils;

public class CommandClassFactory {

	private final static Logger LOGGER = LoggerFactory.getLogger(CommandClassFactory.class);
	
	private static CommandClassFactory INSTANCE;
	
	private Map<Integer, Constructor<?>> constructors = new HashMap<>();
	
	public CommandClassFactory() {
		
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AnnotationTypeFilter(CommandClassMeta.class));
		
		Set<BeanDefinition> beanDefinitions = provider.findCandidateComponents(CommandClass.class.getPackageName());
		for (BeanDefinition beanDefinition : beanDefinitions) {
			
			LOGGER.info("Class name: {}", beanDefinition.getBeanClassName());
			try {
				Class<?> clazz = ClassUtils.getDefaultClassLoader().loadClass(beanDefinition.getBeanClassName());
				CommandClassMeta meta = clazz.getAnnotation(CommandClassMeta.class);
				if (meta != null) {
					Constructor<?> constructor = clazz.getConstructor(int.class, ZwaveNode.class);
					if (constructor == null) {
						LOGGER.warn("Command Class {} does not have a ZwaveNode constructor", BitUtils.toHex(meta.id()));
						continue;
					}
					
					constructors.put(meta.id(), constructor);
				}
			} catch (Exception e) {
				LOGGER.error("Failed to discover {} - {}", beanDefinition.getBeanClassName(), e.getMessage());
			}
		}		
	}
	
	public static CommandClass create(ZwaveNode node, int commandClassId) {
		return create(node, commandClassId, 1);
	}
	
	public static CommandClass create(ZwaveNode node, VersionedCommandClass versionedCommandClass) {
		return create(node, versionedCommandClass.getCommandClassId(), versionedCommandClass.getVersion());
	}
	
	public static CommandClass create(ZwaveNode node, int commandClassId, int supportedVersion) {
		
		if (INSTANCE == null) {
			INSTANCE = new CommandClassFactory();
		}
		
		return INSTANCE.buildClass(node, commandClassId, supportedVersion);
	}
	
	
	private CommandClass buildClass(ZwaveNode node, int commandClassId, int supportedVersion) {
		
		Constructor<?> constructor = constructors.get(commandClassId);
		if (constructor == null) {
			return null;
		}
		
		try {
			return (CommandClass)constructor.newInstance(supportedVersion, node);
		} catch (Exception e) {
			LOGGER.error("Failed to instantiate command class {} - {}", BitUtils.toHex(commandClassId), e);
			return null;
		}
	}
}
