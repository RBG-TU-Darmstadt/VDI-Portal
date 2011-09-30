vdi = {

	modalOptions: {
		backdrop: true,
		keyboard: true
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

		// Create dialog
		$('#vdi-create-vm-dialog').modal(this.modalOptions);
		$('.vdi-create-vm').click($.proxy(this, 'initCreateDialog'));
		$('#vdi-create-vm-dialog').bind('hidden', $.proxy(this, 'resetCreateDialog'));
		$('#vdi-create-vm-type-family').change($.proxy(this, 'populateVMTypes'));
		$('#vdi-create-vm-dialog .secondary').click(this.hideDialog);
		$('#vdi-create-vm-dialog form').submit($.proxy(this, 'createVM'));

		// Edit dialog
		$('#vdi-edit-vm-dialog').modal(this.modalOptions);
		$('.vdi-machine-edit').live('click', $.proxy(this, 'initEditVMDialog'));
		$('#vdi-edit-vm-dialog .secondary').click(this.hideDialog);
		$('#vdi-edit-vm-dialog form').submit($.proxy(this, 'editVM'));

		// Mount dialog
		$('#vdi-mount-image-dialog').modal(this.modalOptions);
		$('.vdi-machine-mount').live('click', $.proxy(this, 'initMountImageDialog'));
		$('#vdi-mount-image-dialog').bind('hidden', $.proxy(this, 'resetMountImageDialog'));
		$('#vdi-mount-image-dialog .secondary').click(this.hideDialog);
		$('#vdi-mount-image-dialog form').submit($.proxy(this, 'mountImage'));

		// Delete dialog
		$('#vdi-delete-vm-dialog').modal(this.modalOptions);
		$('.vdi-machine-remove').live('click', $.proxy(this, 'initRemoveVMDialog'));
		$('#vdi-delete-vm-dialog .secondary').click(this.hideDialog);
		$('#vdi-delete-vm-dialog .danger').click($.proxy(this, 'removeVM'));

		// Tag navigation
		$('ul.tag-nav li a, div.vdi-machine span.vdi-machine-tags a').live('click', $.proxy(this, 'showVMsWithTag'));

		// VM list buttons
		$('.vdi-machine-start, .vdi-machine-unpause').live('click', $.proxy(this, 'startVM'));
		$('.vdi-machine-pause').live('click', $.proxy(this, 'pauseVM'));
		$('.vdi-machine-stop').live('click', $.proxy(this, 'stopVM'));
		$('.vdi-machine-eject').live('click', $.proxy(this, 'unmountImage'));

		// Load VMs
		this.getVMs();

		// Register screenshot refresher
		setInterval($.proxy(this, 'refreshVMScreenshots'), 5*1000);
	},

	hideDialog: function() {
		$(this).closest('.modal').modal('hide');
	},

	initCreateDialog: function() {
		// Init tagging
		this.initTagInput($("#vdi-create-vm-tags"));

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

		// Init sliders
		this.initResourceSliders(
			$('#vdi-create-vm-memory').text('256 MB'),
			$('#vdi-create-vm-harddrive').text('5 GB'),
			$('#vdi-create-vm-vram').text('32 MB')
		);

		// Show dialog
		$('#vdi-create-vm-dialog').modal('show');
	},

	populateVMTypes: function(event) {
		var types = $('#vdi-create-vm-type-family').data('vm_types')[$(event.target).val()];

		var typeSelect = $('#vdi-create-vm-type');
		typeSelect.empty();

		$.each(types, function(typeName, typeDescription) {
			typeSelect.append("<option value='" + typeName + "'>" + typeDescription + "</option>");
		});
	},

	initResourceSliders: function(memoryInput, hddInput, vramInput) {
		Manager.getRestrictions(function(json) {
			var response = $.parseJSON(json);

			if (response.success) {
				// Memory slider
				var memoryValues = [4, 8, 16, 32, 64, 128, 384, 256, 512, 786, 1024, 1536, 2048, 3072, 4096, 6144, 8192],
					minMemory = memoryValues.indexOf(response.restrictions.minMemory),
					maxMemory = memoryValues.indexOf(response.restrictions.maxMemory);
				memoryValues = memoryValues.splice(minMemory, maxMemory - minMemory + 1);
				memoryInput.prev('.vdi-slider').slider({
					value: memoryValues.indexOf(parseInt(memoryInput.text(), 10)),
					min: 0,
					max: memoryValues.length-1,
					step: 1,
					slide: function(event, ui) {
						memoryInput.text(memoryValues[ui.value] + " MB");
					}
				});

				if(hddInput != null) {
					// HDD slider
					hddInput.prev('.vdi-slider').slider({
						value: parseInt(hddInput.text(), 10),
						min: response.restrictions.minHdd / 1024,
						max: response.restrictions.maxHdd / 1024,
						step: 1,
						slide: function(event, ui) {
							hddInput.text(ui.value + " GB");
						}
					});
				}

				// VRam slider
				var vramValues = [4, 8, 16, 32, 64, 128, 384, 256, 512],
					minVRam = vramValues.indexOf(response.restrictions.minVRam),
					maxVRam = vramValues.indexOf(response.restrictions.maxVRam);
				vramValues = vramValues.splice(minVRam, maxVRam - minVRam + 1);
				vramInput.prev('.vdi-slider').slider({
					value: vramValues.indexOf(parseInt(vramInput.text(), 10)),
					min: 0,
					max: vramValues.length-1,
					step: 1,
					slide: function(event, ui) {
						vramInput.text(vramValues[ui.value] + " MB");
					}
				});
			}
		});
	},

	initTagInput: function(tagInput) {
		var self = this;

		// Start autosuggest plugin on tag input field
		Manager.getTags(function(json) {
			var response = $.parseJSON(json);

			if (response.success) {
				var tags = response.tags;

		 		tagInput
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
	},

	tags: undefined,

	getVMs: function() {
		// Unmark all anchors in tag navigation
		$("ul.tag-nav li").removeClass('active');

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
					tags_dom += "<li " + (identifier == 'all' ? "class='active'" : "")+ ">"
						+ "<a href='#tag/" + identifier + "'>"
						+ tag.name + " (" + tag.vms.length + ")</a></li>";
				});
				$('ul.tag-nav').html(tags_dom);
			}
		});
	},

	showVMsWithTag: function(event) {
		event.preventDefault();

		var href = $(event.target).attr('href');
		var tagIdentifier = href.match(/#tag\/(.+)/)[1];

		// Unmark all anchors in tag navigation
		$("ul.tag-nav li").removeClass('active');

		// Mark active tag
		$("ul.tag-nav li a[href='" + href + "']").closest('li').addClass('active');

		// Render VMs matching the tag
		this.renderVMs(this.tags[tagIdentifier].vms);
	},

	buttons: {
			start:		"<button class='btn success vdi-machine-start'>Start</button>",
			pause:		"<button class='btn vdi-machine-pause'>Pause</button>",
			unpause:	"<button class='btn success vdi-machine-unpause'>Fortsetzen</button>",
			stop:		"<button class='btn danger vdi-machine-stop'>Stop</button>",
			disk:		"<button class='btn info vdi-machine-mount'>Mount Image</button>",
			eject:		"<button class='btn info vdi-machine-eject'>Eject Image</button>",
			rdp:		"<button class='btn vdi-machine-eject'>RDP Link</button>"
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
				active_buttons = [self.buttons.unpause];
				show_paused = true;
			}

			var vmDom = $("<div class='well vdi-machine' id='vdi-machine-id-" + vm.id + "'>"
			+ "	<div class='vdi-machine-buttons'>"
			+ "		<span class='vdi-machine-edit'></span>"
			+ "		<span class='vdi-machine-remove'></span>"
			+ "	</div>"
			+ "	<div class='vdi-machine-screenshot'>"
			+ "		<img src='" + screenshot + "'>"
			+ 		(show_paused && "<img src='../resources/images/machine-paused.png'>")
			+ "	</div>"
			+ "	<div class='vdi-machine-container'>"
			+ "		<div class='vdi-machine-actions'>"
			+ active_buttons.join("\n")
			+ "		</div>"
			+ "		<div class='vdi-machine-infos'>"
			+ "			<span class='vdi-machine-info-title'>Name:</span> " + vm.name + "<br />"
			+ "			<span class='vdi-machine-info-title'>Beschreibung:</span> " + vm.description + "<br />"
			+ "			<span class='vdi-machine-info-title'>Tags:</span> "
			+ "			<span class='vdi-machine-tags'>" + vm_tags.join(", ") + "</span><br />"
			+ "			<span class='vdi-machine-info-title'>Status:</span> " + status + "<br />"
			+ 			(rpd_url && "<span class='vdi-machine-info-title'>RDP:</span> " + rpd_url + "<br />")
			+ 			(image && "<span class='vdi-machine-info-title'>Image:</span> " + image + "<br />")
			+ "		</div>"
			+ "	</div>"
			+ "<div class='clear-layout'></div>"
			+ "</div>");

			// Add hidden data for VM
			vmDom.data("vm", vm);

			vmDrawer.append(vmDom);
		});
	},

	createVM: function(event) {
		event.preventDefault();

		var name = $.trim($("#vdi-create-vm-name").val());
		var description = $.trim($("#vdi-create-vm-description").val());
		var type = $("#vdi-create-vm-type").val();
		var image = $("#vdi-create-vm-image").val();
		var memory = $("#vdi-create-vm-memory").text();
		var harddrive = $("#vdi-create-vm-harddrive").text();
		var vram = $("#vdi-create-vm-vram").text();
		var acceleration2d = $("#vdi-create-vm-2d-acceleration").prop("checked");
		var acceleration3d = $("#vdi-create-vm-3d-acceleration").prop("checked");
		var tags = $("#vdi-create-vm-tags").val();

		// Validate
		if (type == null || type == '') {
			$("#vdi-create-vm-type-family, #vdi-create-vm-type").closest('div.clearfix').addClass('error');

			return;
		}

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
				$('#vdi-create-vm-dialog').modal('hide');

				// Reload VMs
				self.getVMs();
			}
		});
	},

	getMachineData: function(event) {
		return $(event.target).closest('.vdi-machine').data('vm');
	},

	startVM: function(event) {
		var id = this.getMachineData(event).id;

		this.showActionActivity(event.target);

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

		this.showActionActivity(event.target);

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

		this.showActionActivity(event.target);

		var self = this;
		Manager.stopVM(id, function(json) {
			var response = $.parseJSON(json);

			if (response.success) {
				// Reload VMs
				self.getVMs();
			}
		});
	},

	initRemoveVMDialog: function(event) {
		var vm = this.getMachineData(event);
		$('#vdi-delete-vm-machine-id').val(vm.id);

		$('#vdi-delete-vm-name').text(vm.name);

		$('#vdi-delete-vm-dialog').modal('show');
	},

	removeVM: function() {
		var id = $('#vdi-delete-vm-machine-id').val();

		var self = this;
		Manager.removeVM(id, function(json) {
			var response = $.parseJSON(json);

			if (response.success) {
				$('#vdi-delete-vm-dialog').modal('hide');

				// Reload VMs
				self.getVMs();
			} else {
				alert('VM konnte nicht entfernt werden, läuft sie evtl. noch?');
			}
		});
	},

	initEditVMDialog: function(event) {
		var vm = this.getMachineData(event);
		$('#vdi-edit-vm-machine-id').val(vm.id);

		// Populate inputs
		$('#vdi-edit-vm-name').val(vm.name);
		$('#vdi-edit-vm-description').val(vm.description);
		$('#vdi-edit-vm-memory').text(vm.memory + " MB");
		$('#vdi-edit-vm-vram').text(vm.vram + " MB");
		$('#vdi-edit-vm-2d-acceleration').prop("checked", vm.accelerate2d);
		$('#vdi-edit-vm-3d-acceleration').prop("checked", vm.accelerate3d);
		var tags = $.map(vm.tags, function(tag) {
			return tag.name;
		});
		$('#vdi-edit-vm-tags').val(tags.join(', '));

		// Init tagging
		this.initTagInput($("#vdi-edit-vm-tags"));

		// Init sliders
		this.initResourceSliders(
			$('#vdi-edit-vm-memory'),
			null,
			$('#vdi-edit-vm-vram')
		);

		$('#vdi-edit-vm-dialog').modal('show');
	},

	editVM: function(event) {
		event.preventDefault();

		var id = $('#vdi-edit-vm-machine-id').val();

		this.showActionActivity($('#vdi-machine-id-' + id).find('.vdi-machine-edit'));

		var name = $.trim($("#vdi-edit-vm-name").val());
		var description = $.trim($("#vdi-edit-vm-description").val());
		var memory = $("#vdi-edit-vm-memory").text();
		var vram = $("#vdi-edit-vm-vram").text();
		var acceleration2d = $("#vdi-edit-vm-2d-acceleration").prop("checked");
		var acceleration3d = $("#vdi-edit-vm-3d-acceleration").prop("checked");
		var tags = $("#vdi-edit-vm-tags").val();

		// Sanitize input
		memory = parseInt(memory, 10);
		vram = parseInt(vram, 10);
		tags = this.cleanTags(this.split(tags));

		var self = this;
		Manager.editVM(id, name, description, memory, vram, acceleration2d,
				acceleration3d, tags, function(json) {
			var response = $.parseJSON(json);

			if (response.success) {
				$('#vdi-edit-vm-dialog').modal('hide');

				// Reload VMs
				self.getVMs();
			}
		});
	},

	resetCreateDialog: function() {
		$('#vdi-create-vm-name').val('');
		$('#vdi-create-vm-description').val('');
		$("#vdi-create-vm-type-family option[value!='']").remove();
		$('#vdi-create-vm-type').val('');
		$('#vdi-create-vm-image').empty();
		$('#vdi-create-vm-memory').text('');
		$('#vdi-create-vm-harddrive').text('');
		$('#vdi-create-vm-vram').text('');
		$('#vdi-create-vm-2d-acceleration').prop("checked", false);
		$('#vdi-create-vm-3d-acceleration').prop("checked", false);
		$('#vdi-create-vm-tags').val('');

		$('#vdi-create-vm-dialog div.clearfix').removeClass('error');

		// Clear machine types
		$('#vdi-create-vm-type').empty();
	},

	refreshVMScreenshots: function() {
		$('div.vdi-machine').each(function() {
			var $elem = $(this);

			if($elem.data('vm').status == 'STARTED') {
				var image = $elem.find('div.vdi-machine-screenshot img');

				image.attr('src', image.attr('src').replace(/(.*)&[0-9]+/, "$1&" + (new Date()).getTime()));
			}
		});
	},

	initMountImageDialog: function(event) {
		var vm = this.getMachineData(event);

		$('#vdi-mount-image-machine-id').val(vm.id);
		$('#vdi-mount-image-name').text(vm.name);

		this.populateImages($('#vdi-mount-image-identifier'));

		$('#vdi-mount-image-dialog').modal('show');
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
		event.preventDefault();

		var id = $("#vdi-mount-image-machine-id").val();
		var image = $("#vdi-mount-image-identifier").val();

		this.showActionActivity($('#vdi-machine-id-' + id).find('.vdi-machine-mount'));

		var self = this;
		Manager.mountImage(id, image, function(json) {
			var response = $.parseJSON(json);

			if (response.success) {
				$('#vdi-mount-image-dialog').modal('hide');

				// Reload VMs
				self.getVMs();
			}
		});
	},

	unmountImage: function(event) {
		var id = this.getMachineData(event).id;

		this.showActionActivity(event.target);

		var self = this;
		Manager.unmountImage(id, function(json) {
			var response = $.parseJSON(json);

			if (response.success) {
				// Reload VMs
				self.getVMs();
			}
		});
	},

	resetMountImageDialog: function() {
		// Clear images
		$('#vdi-mount-image-identifier').empty();
	},

	showActionActivity: function(target) {
		$(target).closest('.vdi-machine').find('.vdi-machine-actions').html('<img src="../resources/images/ajax-loader.gif">');
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