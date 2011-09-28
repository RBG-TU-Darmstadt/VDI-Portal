vdi = {

	fancyboxOptions: {
		overlayShow: true,
		hideOnOverlayClick: false,
		hideOnContentClick: false,
		enableEscapeButton: true,
		showCloseButton: true
	},

	init: function() {
		// Enable Reverse AJAX with DWR
		dwr.engine.setActiveReverseAjax(true);
		dwr.engine.setErrorHandler(function(message, exception) {
			if (typeof window.console != undefined) {
				console.log("Error message is: " + message + " - Error Details: " + dwr.util.toDescriptiveString(exception, 2));
			}
		});

		// Register with manager
		Manager.register();

		$('.vdi-create-vm').fancybox($.extend({
			onStart: $.proxy(this, 'initCreateDialog'),
			onClosed: $.proxy(this, 'resetCreateDialog')
		}, this.fancyboxOptions));
		$('#vdi-create-vm-type-family').change($.proxy(this, 'populateVMTypes'));

		$('.vdi-mount-image').fancybox($.extend({
			onStart: $.proxy(this, 'initMountImageDialog'),
			onClosed: $.proxy(this, 'resetImageDialog')
		}, this.fancyboxOptions));

		// Tag navigation
		$('div.vdi-nav ul li a, div.vdi-machine span.vdi-machine-tags a').live('click', $.proxy(this, 'showVMsWithTag'));

		// Load VMs
		this.getVMs();

		// VM list buttons
		$('.vdi-machine-start').live('click', $.proxy(this, 'startVM'));
		$('.vdi-machine-pause').live('click', $.proxy(this, 'pauseVM'));
		$('.vdi-machine-stop').live('click', $.proxy(this, 'stopVM'));
		$('.vdi-machine-remove').live('click', $.proxy(this, 'removeVM'));
		$('.vdi-machine-mount').live('click', $.proxy(this, 'prepareMountImage'));
		$('.vdi-machine-eject').live('click', $.proxy(this, 'unmountImage'));

		// Dialog buttons
		$('.vdi-create-vm-button').click($.proxy(this, 'createVM'));
		$('.vdi-mount-image-button').click($.proxy(this, 'mountImage'));

		// Register screenshot refresher
		setInterval($.proxy(this, 'refreshVMScreenshots'), 5*1000);
	},
	
	initCreateDialog: function() {
		var self = this;

		// Start autosuggest plugin on tag input field
		Manager.getTags(function(json) {
			var response = $.parseJSON(json);

			if (response.success) {
				var tags = response.tags;

		 		$("#vdi-create-vm-tags")
		 			// don't navigate away from the field on tab when selecting an item
		 			.bind("keydown", function(event) {
		 				if (event.keyCode === $.ui.keyCode.TAB &&
		 						$(this).data("autocomplete").menu.active) {
		 					event.preventDefault();
		 				}
		 			})
		 			.autocomplete({
		 				minLength: 0,
		 				source: function(request, response) {
		 					var currentTerms = self.split(request.term);
		 					var lastTerm = currentTerms.pop();

		 					// Remove already present terms
		 					var terms = [];
		 					$.each(tags, function(i, tag) {
		 						if (currentTerms.indexOf(tag) == -1) {
		 							terms.push(tag);
		 						}
		 					});

		 					// delegate back to autocomplete, but extract the last term
		 					response($.ui.autocomplete.filter(terms,  lastTerm));
		 				},
		 				focus: function() {
		 					// prevent value inserted on focus
		 					return false;
		 				},
		 				select: function(event, ui) {
		 					var terms = self.split(this.value);

		 					// remove the current input
		 					terms.pop();

		 					// add the selected item
		 					terms.push(ui.item.value);

		 					// add placeholder to get the comma-and-space at the end
		 					terms.push("");
		 					this.value = terms.join(", ");

		 					return false;
		 				}
		 			});
			}
		});

		// Populate VM type families
		Manager.getVMTypes(function(json) {
			var response = $.parseJSON(json);

			if (response.success) {
				var familySelect = $('#vdi-create-vm-type-family');

				familySelect.data('vm_types', response.types);

				$.each(response.types, function(familyName, familyTypes) {
					familySelect.append("<option value='" + familyName + "'>" + familyName + "</option>");
				});
			}
		});

		// Populate ISO images
		var imageSelect = $('#vdi-create-vm-image');
		imageSelect.append("<option value=''>keines</option>");
		this.populateImages(imageSelect);

		// Retrieve resource restrictions
		Manager.getRestrictions(function(json) {
			var response = $.parseJSON(json);

			if (response.success) {
				// Memory slider
				var memoryValues = [4, 8, 16, 32, 64, 128, 384, 256, 512, 786, 1024, 1536, 2048, 3072, 4096, 6144, 8192],
					minMemory = memoryValues.indexOf(response.restrictions.minMemory),
					maxMemory = memoryValues.indexOf(response.restrictions.maxMemory);
				memoryValues = memoryValues.splice(minMemory, maxMemory - minMemory + 1);
				$('#vdi-create-vm-memory-slider').slider({
					value: 5,
					min: 0,
					max: memoryValues.length-1,
					step: 1,
					slide: function(event, ui) {
						$("#vdi-create-vm-memory").val(memoryValues[ui.value] + " MB");
					}
				});
				$("#vdi-create-vm-memory").val(memoryValues[5] + " MB");

				// HDD slider
				$('#vdi-create-vm-harddrive-slider').slider({
					value: 5,
					min: response.restrictions.minHdd / 1024,
					max: response.restrictions.maxHdd / 1024,
					step: 1,
					slide: function(event, ui) {
						$("#vdi-create-vm-harddrive").val(ui.value + " GB");
					}
				});
				$("#vdi-create-vm-harddrive").val(5 + " GB");

				// VRam slider
				var vramValues = [4, 8, 16, 32, 64, 128, 384, 256, 512],
					minVRam = vramValues.indexOf(response.restrictions.minVRam),
					maxVRam = vramValues.indexOf(response.restrictions.maxVRam);
				vramValues = vramValues.splice(minVRam, maxVRam - minVRam + 1);
				$('#vdi-create-vm-vram-slider').slider({
					value: 2,
					min: 0,
					max: vramValues.length-1,
					step: 1,
					slide: function(event, ui) {
						$("#vdi-create-vm-vram").val(vramValues[ui.value] + " MB");
					}
				});
				$("#vdi-create-vm-vram").val(vramValues[2] + " MB");
			}
		});
	},
	
	populateVMTypes: function(event) {
		var types = $('#vdi-create-vm-type-family').data('vm_types')[$(event.target).val()];

		var typeSelect = $('#vdi-create-vm-type');
		typeSelect.empty();

		$.each(types, function(typeName, typeDescription) {
			typeSelect.append("<option value='" + typeName + "'>" + typeDescription + "</option>");
		});
	},

	tags: undefined,

	getVMs: function() {
		// Unmark all anchors in tag navigation
		$("div.vdi-nav ul li a").removeClass('vdi-tag-active');

		this.tags = {
			all: {
				name: 'all',
				vms: []
			}
		};

		var self = this;
		Manager.getVMs(function(json) {
			var response = $.parseJSON(json);

			if (response.success) {
				// Register tags
				$.each(response.vms, function(i, vm) {
					$.each(vm.tags, function(i, tag) {
						if ( ! (tag.identifier in self.tags)) {
							self.tags[tag.identifier] = {
								name: tag.name,
								vms: [vm]
							};
						} else {
							self.tags[tag.identifier].vms.push(vm);
						}
					});

					self.tags['all'].vms.push(vm);
				});

				self.renderVMs(response.vms);

				// Initialize tag navigation
				var tags_dom = '';
				$.each(self.tags, function(identifier, tag) {
					tags_dom += "<li><a href='#tag/" + identifier + "' "
						+ (identifier == 'all' ? "class='vdi-tag-active'" : "") + ">"
						+ tag.name + " (" + tag.vms.length + ")</a></li>";
				});
				$('div.vdi-nav ul').html(tags_dom);
			}
		});
	},

	showVMsWithTag: function(event) {
		event.preventDefault();

		var href = $(event.target).attr('href');
		var tagIdentifier = href.match(/#tag\/(.+)/)[1];

		// Unmark all anchors in tag navigation
		$("div.vdi-nav ul li a").removeClass('vdi-tag-active');

		// Mark active tag
		$("div.vdi-nav ul li a[href='" + href + "']").addClass('vdi-tag-active');

		// Render VMs matching the tag
		this.renderVMs(this.tags[tagIdentifier].vms);
	},

	buttons: {
			start:	"<span class='vdi-machine-start'><img src=\"../resources/images/play.png\"></span>",
			pause:	"<span class='vdi-machine-pause'><img src=\"../resources/images/pause.png\"></span>",
			stop:	"<span class='vdi-machine-stop'><img src=\"../resources/images/stop.png\"></span>",
			disk:	"<span class='vdi-machine-mount'><img src=\"../resources/images/disk.png\"></span>",
			eject:	"<span class='vdi-machine-eject'><img src=\"../resources/images/eject.png\"></span>",
			rdp:	"<img src=\"../resources/images/eye.png\">"
	},

	renderVMs: function(vms) {
		var vmDrawer = $('.vdi-machine-drawer-machines');

		// Clear machines
		vmDrawer.empty();

		var self = this;
		$.each(vms, function(i, vm) {
			var vm_tags = [];
			$.each(vm.tags, function(i, tag) {
				vm_tags.push("<a href='#tag/" + tag.identifier + "'>" + tag.name + "</a>");
			});

			// Visualize VM status
			var status = '',
				active_buttons = [],
				screenshot = "./screenshot/?machine=" + vm.id + "&width=120&height=90&" + (new Date()).getTime(),
				show_paused = "", // false
				rpd_url = "",
				image = vm.image || "",
				disk = vm.image ? self.buttons.eject : self.buttons.disk;

			if (vm.status == 'STARTED') {
				status = "Läuft";
				rpd_url = vm.rdp_url;
				var rdp = "<a href=\"./rdp/?machine=" + vm.id + "\" target=\"_blank\">" + self.buttons.rdp + "</a>";
				active_buttons = [rdp, self.buttons.pause, disk, self.buttons.stop];
			} else if (vm.status == 'STOPPED') {
				status = "Ausgeschaltet";

				if (vm.last_active) {
					status += ", zuletzt gestartet " + self.formatDate(vm.last_active);
				}

				active_buttons = [self.buttons.start, disk];
				screenshot = "../resources/images/machine-off.png";
			} else if (vm.status == 'PAUSED') {
				status = "Angehalten";
				active_buttons = [self.buttons.start];
				show_paused = true;
			}

			var vmDom = $("<div class='vdi-machine'>"
			+ "	<div class='vdi-machine-remove'><img src=\"../resources/images/delete.png\"></div>"
			+ "	<div class='vdi-machine-edit'><img src=\"../resources/images/edit.png\"></div>"
			+ "	<div class='vdi-machine-screenshot'>"
			+ "		<img src='" + screenshot + "'>"
			+ 		(show_paused && "<img src='../resources/images/machine-paused.png'>")
			+ "	</div>"
			+ "	<div class='vdi-machine-infos'>"
			+ "		<div class='vdi-machine-actions'>"
			+ active_buttons.join("\n")
			+ "		</div>"
			+ "		<span class='vdi-machine-info-title'>Name:</span> " + vm.name + "<br />"
			+ "		<span class='vdi-machine-info-title'>Beschreibung:</span> " + vm.description + "<br />"
			+ "		<span class='vdi-machine-info-title'>Tags:</span> "
			+ "			<span class='vdi-machine-tags'>" + vm_tags.join(", ") + "</span><br />"
			+ "		<span class='vdi-machine-info-title'>Status:</span> " + status + "<br />"
			+ 		(rpd_url && "<span class='vdi-machine-info-title'>RDP:</span> " + rpd_url + "<br />")
			+ 		(image && "<span class='vdi-machine-info-title'>Image:</span> " + image + "<br />")
			+ "	</div>"
			+ "<div class='clear-layout'></div>"
			+ "</div>");

			// Add hidden data for VM
			vmDom.data("id", vm.id);
			vmDom.data("status", vm.status);
			vmDom.data("name", vm.name);

			vmDrawer.append(vmDom);
		});
	},
	
	createVM: function() {
		var name = $("#vdi-create-vm-name").val();
		var description = $("#vdi-create-vm-description").val();
		var type = $("#vdi-create-vm-type").val();
		var image = $("#vdi-create-vm-image").val();
		var memory = $("#vdi-create-vm-memory").val();
		var harddrive = $("#vdi-create-vm-harddrive").val();
		var vram = $("#vdi-create-vm-vram").val();
		var acceleration2d = $("#vdi-create-vm-2d-acceleration").prop("checked");
		var acceleration3d = $("#vdi-create-vm-3d-acceleration").prop("checked");
		var tags = $("#vdi-create-vm-tags").val();

		// Sanitize input
		memory = parseInt(memory, 10);
		harddrive = parseInt(parseFloat(harddrive.replace(',', '.'), 10) * 1024, 10);
		vram = parseInt(vram, 10);
		tags = this.cleanTags(this.split(tags));

		var self = this;
		Manager.createVM(name, description, type, image, memory, harddrive,
				vram, acceleration2d, acceleration3d, tags, function(json) {
			var response = $.parseJSON(json);

			if (response.success) {
				$.fancybox.close();

				// Reload VMs
				self.getVMs();
			}
		});
	},
	
	getMachineData: function(event) {
		return $(event.target).parents('.vdi-machine').data();
	},
	
	startVM: function(event) {
		var id = this.getMachineData(event).id;

		var self = this;
		Manager.startVM(id, function(json) {
			var response = $.parseJSON(json);

			if (response.success) {
				// Reload VMs
				self.getVMs();
			} else {
				alert("Leider stehen nicht genug Resourcen zur Verfügung! "
						+ "Bitte versuchen sie es später erneut.");
			}
		});
	},
	
	pauseVM: function(event) {
		var id = this.getMachineData(event).id;

		var self = this;
		Manager.pauseVM(id, function(json) {
			var response = $.parseJSON(json);

			if (response.success) {
				// Reload VMs
				self.getVMs();
			}
		});
	},
	
	stopVM: function(event) {
		var id = this.getMachineData(event).id;

		var self = this;
		Manager.stopVM(id, function(json) {
			var response = $.parseJSON(json);

			if (response.success) {
				// Reload VMs
				self.getVMs();
			}
		});
	},
	
	removeVM: function(event) {
		var confirmation = confirm("VM wirklich entfernen?");

		if ( ! confirmation)
			return;

		var id = this.getMachineData(event).id;

		var self = this;
		Manager.removeVM(id, function(json) {
			var response = $.parseJSON(json);

			if (response.success) {
				// Reload VMs
				self.getVMs();
			} else {
				alert("Leider stehen nicht genug Resourcen zur Verfügung! "
						+ "Bitte versuchen sie es später erneut.");
			}
		});
	},
	
	resetCreateDialog: function() {
		$('#vdi-create-vm-name').val('');
		$('#vdi-create-vm-description').val('');
		$("#vdi-create-vm-type-family option[value!='']").remove();
		$('#vdi-create-vm-type').val('');
		$('#vdi-create-vm-image').empty();
		$('#vdi-create-vm-memory').val('');
		$('#vdi-create-vm-harddrive').val('');
		$('#vdi-create-vm-vram').val('');
		$('#vdi-create-vm-2d-acceleration').prop("checked", false);
		$('#vdi-create-vm-3d-acceleration').prop("checked", false);
		$('#vdi-create-vm-tags').val('');

		// Clear machine types
		$('#vdi-create-vm-type').empty();
	},

	refreshVMScreenshots: function() {
		$('div.vdi-machine').each(function() {
			var $elem = $(this);

			if($elem.data('status') == 'STARTED') {
				var image = $elem.find('div.vdi-machine-screenshot img');

				image.attr('src', image.attr('src').replace(/(.*)&[0-9]+/, "$1&" + (new Date()).getTime()));
			}
		});
	},

	prepareMountImage: function(event) {
		$('#vdi-mount-image-dialog #vdi-mount-image-machine-id').val(this.getMachineData(event).id);
		$('#vdi-mount-image-dialog .vdi-mount-image-machine-name').text(this.getMachineData(event).name);

		$('.vdi-mount-image').click();
	},

	initMountImageDialog: function() {
		this.populateImages($('#vdi-mount-image-identifier'));
	},

	populateImages: function(imageSelect) {
		Manager.getImages(function(json) {
			var response = $.parseJSON(json);

			if (response.success) {
				$.each(response.images, function(i, name) {
					imageSelect.append("<option value='" + name + "'>" + name + "</option>");
				});
			}
		});
	},

	mountImage: function(event) {
		var id = $("#vdi-mount-image-machine-id").val();
		var image = $("#vdi-mount-image-identifier").val();

		var self = this;
		Manager.mountImage(id, image, function(json) {
			var response = $.parseJSON(json);

			if (response.success) {
				$.fancybox.close();

				// Reload VMs
				self.getVMs();
			}
		});
	},

	unmountImage: function(event) {
		var id = this.getMachineData(event).id;

		var self = this;
		Manager.unmountImage(id, function(json) {
			var response = $.parseJSON(json);

			if (response.success) {
				// Reload VMs
				self.getVMs();
			}
		});
	},

	resetImageDialog: function() {
		$('#vdi-mount-image-machine-id').val('');

		// Clear images
		$('#vdi-mount-image-identifier').empty();
	},

	formatDate: function(timestamp) {
		var date = new Date(timestamp);

		return this.padDate(date.getDate()) + "." + this.padDate(date.getMonth() + 1) + "." + this.padDate(date.getFullYear())
			+ ", " + this.padDate(date.getHours()) + ":" + this.padDate(date.getMinutes());
	},

	padDate: function(num) {
		return ("0" + num).slice(-2);
	},

	split: function(val) {
		return val.split(/,\s*/);
	},

	cleanTags: function (arr) {
		var i,
			len=arr.length,
			out=[],
			obj={};

		// Remove duplicates and empty entries
		for (i=0;i<len;i++) {
			if (arr[i] != "")
				obj[arr[i]]=0;
			
		}
		for (i in obj) {
			out.push(i);
		}

		return out;
	}

};

$(document).ready(function() {
	vdi.init();
});